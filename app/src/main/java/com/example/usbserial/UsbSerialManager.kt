package com.example.usbserial

import android.content.Context
import android.hardware.usb.*
import android.util.Log
import kotlinx.coroutines.*
import java.io.IOException

class UsbSerialManager(private val context: Context) {
    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private var usbDevice: UsbDevice? = null
    private var usbConnection: UsbDeviceConnection? = null
    private var usbInterface: UsbInterface? = null
    private var endpointIn: UsbEndpoint? = null
    private var endpointOut: UsbEndpoint? = null
    
    // Callback para dados recebidos
    var onDataReceived: ((String) -> Unit)? = null
    
    private var readingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    companion object {
        private const val TAG = "UsbSerialManager"
        private const val TIMEOUT = 1000
        private const val BUFFER_SIZE = 1024
    }
    
    fun connectToDevice(device: UsbDevice): Boolean {
        Log.i(TAG, "=== Tentando conectar ao dispositivo ===")
        Log.i(TAG, "Device: ${device.productName}")
        Log.i(TAG, "VID: 0x${String.format("%04X", device.vendorId)}")
        Log.i(TAG, "PID: 0x${String.format("%04X", device.productId)}")
        
        try {
            // Verificar permissão
            if (!usbManager.hasPermission(device)) {
                Log.e(TAG, "Sem permissão USB")
                return false
            }
            
            // Desconectar dispositivo anterior
            disconnect()
            
            // Encontrar interface adequada
            var targetInterface: UsbInterface? = null
            var inEndpoint: UsbEndpoint? = null
            var outEndpoint: UsbEndpoint? = null
            
            for (i in 0 until device.interfaceCount) {
                val iface = device.getInterface(i)
                Log.d(TAG, "Interface $i: classe=${iface.interfaceClass}")
                
                // Procurar por CDC Communication ou Vendor Specific
                if (iface.interfaceClass == UsbConstants.USB_CLASS_CDC_DATA || 
                    iface.interfaceClass == UsbConstants.USB_CLASS_COMM ||
                    iface.interfaceClass == UsbConstants.USB_CLASS_VENDOR_SPEC) {
                    
                    // Procurar endpoints IN e OUT
                    for (j in 0 until iface.endpointCount) {
                        val endpoint = iface.getEndpoint(j)
                        
                        if (endpoint.type == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                            if (endpoint.direction == UsbConstants.USB_DIR_IN) {
                                inEndpoint = endpoint
                                Log.d(TAG, "Endpoint IN encontrado")
                            } else if (endpoint.direction == UsbConstants.USB_DIR_OUT) {
                                outEndpoint = endpoint
                                Log.d(TAG, "Endpoint OUT encontrado")
                            }
                        }
                    }
                    
                    if (inEndpoint != null && outEndpoint != null) {
                        targetInterface = iface
                        break
                    }
                }
            }
            
            if (targetInterface == null || inEndpoint == null || outEndpoint == null) {
                Log.e(TAG, "Interface ou endpoints não encontrados")
                return false
            }
            
            // Conectar
            val connection = usbManager.openDevice(device)
            if (connection == null) {
                Log.e(TAG, "Falha ao abrir conexão USB")
                return false
            }
            
            if (!connection.claimInterface(targetInterface, true)) {
                Log.e(TAG, "Falha ao reivindicar interface")
                connection.close()
                return false
            }
            
            // Salvar referências
            usbDevice = device
            usbConnection = connection
            usbInterface = targetInterface
            endpointIn = inEndpoint
            endpointOut = outEndpoint
            
            Log.i(TAG, "✅ Conexão USB estabelecida com sucesso!")
            
            // Iniciar leitura contínua
            startReading()
            
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao conectar: ${e.message}")
            disconnect()
            return false
        }
    }
    
    fun sendCommand(command: String) {
        val connection = usbConnection
        val endpoint = endpointOut
        
        if (connection == null || endpoint == null) {
            Log.e(TAG, "Não conectado - não é possível enviar comando")
            return
        }
        
        try {
            val data = "$command\r\n".toByteArray()
            val result = connection.bulkTransfer(endpoint, data, data.size, TIMEOUT)
            
            if (result >= 0) {
                Log.i(TAG, "✅ Comando enviado: $command")
                // Chamar callback
                onDataReceived?.invoke("TX: $command")
            } else {
                Log.e(TAG, "❌ Falha ao enviar comando: $command")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao enviar comando: ${e.message}")
        }
    }
    
    private fun startReading() {
        readingJob?.cancel()
        readingJob = scope.launch {
            val buffer = ByteArray(BUFFER_SIZE)
            
            while (isActive && isDeviceConnected()) {
                try {
                    val connection = usbConnection
                    val endpoint = endpointIn
                    
                    if (connection != null && endpoint != null) {
                        val result = connection.bulkTransfer(endpoint, buffer, buffer.size, TIMEOUT)
                        
                        if (result > 0) {
                            val data = String(buffer, 0, result).trim()
                            if (data.isNotEmpty()) {
                                Log.d(TAG, "📥 Dados recebidos: $data")
                                // Executar callback na thread principal
                                kotlinx.coroutines.Dispatchers.Main.run {
                                    onDataReceived?.invoke("RX: $data")
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    if (isActive) {
                        Log.e(TAG, "Erro na leitura: ${e.message}")
                    }
                }
                
                delay(10) // Pequena pausa
            }
        }
    }
    
    fun disconnect() {
        Log.i(TAG, "Desconectando dispositivo USB")
        
        readingJob?.cancel()
        readingJob = null
        
        usbConnection?.let { connection ->
            usbInterface?.let { iface ->
                connection.releaseInterface(iface)
            }
            connection.close()
        }
        
        usbDevice = null
        usbConnection = null
        usbInterface = null
        endpointIn = null
        endpointOut = null
        
        Log.i(TAG, "✅ Dispositivo desconectado")
    }
    
    fun isDeviceConnected(): Boolean {
        return usbConnection != null && usbDevice != null
    }
    
    fun onDestroy() {
        scope.cancel()
        disconnect()
    }
} 