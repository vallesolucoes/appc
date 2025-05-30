package com.example.usbserial

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    private lateinit var usbManager: UsbManager
    private lateinit var usbSerialManager: UsbSerialManager
    
    // UI Components
    private lateinit var statusText: TextView
    private lateinit var devicesSpinner: Spinner
    private lateinit var connectButton: Button
    private lateinit var disconnectButton: Button
    private lateinit var commandInput: EditText
    private lateinit var sendButton: Button
    private lateinit var terminalText: TextView
    private lateinit var scrollView: ScrollView
    
    private val usbDevices = mutableListOf<UsbDevice>()
    private val terminalOutput = StringBuilder()
    
    companion object {
        private const val TAG = "MainActivity"
        private const val ACTION_USB_PERMISSION = "com.example.usbserial.USB_PERMISSION"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Inicializar componentes
        usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        usbSerialManager = UsbSerialManager(this)
        
        initViews()
        setupClickListeners()
        setupDataCallback()
        
        // Verificar dispositivos USB
        updateDeviceList()
        
        // Registrar receiver para USB
        val filter = IntentFilter().apply {
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            addAction(ACTION_USB_PERMISSION)
        }
        registerReceiver(usbReceiver, filter)
        
        addToTerminal("=== USB Serial Test Iniciado ===")
    }
    
    private fun initViews() {
        statusText = findViewById(R.id.statusText)
        devicesSpinner = findViewById(R.id.devicesSpinner)
        connectButton = findViewById(R.id.connectButton)
        disconnectButton = findViewById(R.id.disconnectButton)
        commandInput = findViewById(R.id.commandInput)
        sendButton = findViewById(R.id.sendButton)
        terminalText = findViewById(R.id.terminalText)
        scrollView = findViewById(R.id.scrollView)
        
        updateConnectionStatus(false)
    }
    
    private fun setupClickListeners() {
        connectButton.setOnClickListener {
            val selectedDevice = getSelectedDevice()
            if (selectedDevice != null) {
                connectToDevice(selectedDevice)
            } else {
                addToTerminal("❌ Nenhum dispositivo selecionado")
            }
        }
        
        disconnectButton.setOnClickListener {
            usbSerialManager.disconnect()
            updateConnectionStatus(false)
            addToTerminal("🔌 Dispositivo desconectado")
        }
        
        sendButton.setOnClickListener {
            val command = commandInput.text.toString().trim()
            if (command.isNotEmpty()) {
                usbSerialManager.sendCommand(command)
                commandInput.setText("")
            }
        }
        
        findViewById<Button>(R.id.refreshButton).setOnClickListener {
            updateDeviceList()
            addToTerminal("🔄 Lista de dispositivos atualizada")
        }
        
        findViewById<Button>(R.id.clearButton).setOnClickListener {
            terminalOutput.clear()
            terminalText.text = ""
        }
    }
    
    private fun setupDataCallback() {
        usbSerialManager.onDataReceived = { data ->
            runOnUiThread {
                addToTerminal(data)
            }
        }
    }
    
    private fun updateDeviceList() {
        usbDevices.clear()
        val deviceMap = usbManager.deviceList
        
        Log.i(TAG, "=== Dispositivos USB encontrados: ${deviceMap.size} ===")
        
        deviceMap.values.forEach { device ->
            Log.i(TAG, "Device: ${device.deviceName}")
            Log.i(TAG, "  Nome: ${device.productName ?: "Desconhecido"}")
            Log.i(TAG, "  VID: 0x${String.format("%04X", device.vendorId)}")
            Log.i(TAG, "  PID: 0x${String.format("%04X", device.productId)}")
            Log.i(TAG, "  Classe: ${device.deviceClass}")
            
            // Adicionar dispositivos que podem ser USB-Serial
            if (isSerialDevice(device)) {
                usbDevices.add(device)
                Log.i(TAG, "  ✅ Adicionado como dispositivo serial")
            }
        }
        
        // Atualizar spinner
        val deviceNames = usbDevices.map { 
            "${it.productName ?: "Desconhecido"} (VID:${String.format("%04X", it.vendorId)})"
        }
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, deviceNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        devicesSpinner.adapter = adapter
        
        statusText.text = "Dispositivos encontrados: ${usbDevices.size}"
        addToTerminal("📱 Encontrados ${usbDevices.size} dispositivos USB-Serial")
    }
    
    private fun isSerialDevice(device: UsbDevice): Boolean {
        // Verificar por VID conhecidos
        val serialVids = setOf(
            0x2341, // Arduino
            0x1A86, // CH340/CH341
            0x0403, // FTDI
            0x10C4, // Silicon Labs CP210x
            0x067B  // Prolific PL2303
        )
        
        if (device.vendorId in serialVids) return true
        
        // Verificar por classe de dispositivo
        if (device.deviceClass == 10 || // CDC Communication
            device.deviceClass == 2 ||  // Communication
            device.deviceClass == 255)  // Vendor specific
            return true
            
        return false
    }
    
    private fun getSelectedDevice(): UsbDevice? {
        val position = devicesSpinner.selectedItemPosition
        return if (position >= 0 && position < usbDevices.size) {
            usbDevices[position]
        } else null
    }
    
    private fun connectToDevice(device: UsbDevice) {
        if (!usbManager.hasPermission(device)) {
            addToTerminal("⚠️ Solicitando permissão USB...")
            requestUsbPermission(device)
            return
        }
        
        addToTerminal("🔗 Conectando a ${device.productName}...")
        
        val success = usbSerialManager.connectToDevice(device)
        updateConnectionStatus(success)
        
        if (success) {
            addToTerminal("✅ Conectado com sucesso!")
            // Enviar comando de teste
            usbSerialManager.sendCommand("AT")
        } else {
            addToTerminal("❌ Falha na conexão")
        }
    }
    
    private fun requestUsbPermission(device: UsbDevice) {
        val permissionIntent = PendingIntent.getBroadcast(
            this, 0, Intent(ACTION_USB_PERMISSION), 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        usbManager.requestPermission(device, permissionIntent)
    }
    
    private fun updateConnectionStatus(connected: Boolean) {
        connectButton.isEnabled = !connected && usbDevices.isNotEmpty()
        disconnectButton.isEnabled = connected
        sendButton.isEnabled = connected
        commandInput.isEnabled = connected
        
        statusText.text = if (connected) {
            "✅ Conectado"
        } else {
            "🔌 Desconectado - Dispositivos: ${usbDevices.size}"
        }
    }
    
    private fun addToTerminal(text: String) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
            
        terminalOutput.append("[$timestamp] $text\n")
        
        // Manter apenas últimas 50 linhas
        val lines = terminalOutput.lines()
        if (lines.size > 50) {
            terminalOutput.clear()
            terminalOutput.append(lines.takeLast(50).joinToString("\n"))
        }
        
        terminalText.text = terminalOutput.toString()
        
        // Scroll para baixo
        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }
    
    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    addToTerminal("🔌 Dispositivo USB conectado")
                    updateDeviceList()
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    addToTerminal("🔌 Dispositivo USB desconectado")
                    updateDeviceList()
                    if (usbSerialManager.isDeviceConnected()) {
                        usbSerialManager.disconnect()
                        updateConnectionStatus(false)
                    }
                }
                ACTION_USB_PERMISSION -> {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                    
                    if (granted && device != null) {
                        addToTerminal("✅ Permissão USB concedida")
                        connectToDevice(device)
                    } else {
                        addToTerminal("❌ Permissão USB negada")
                    }
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(usbReceiver)
        usbSerialManager.onDestroy()
    }
} 