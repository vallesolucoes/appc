# 📱 USB Serial Communication App

[![Build Status](https://github.com/YOUR_USERNAME/usb-serial-android/workflows/Build%20USB%20Serial%20APK/badge.svg)](https://github.com/YOUR_USERNAME/usb-serial-android/actions)
[![Release](https://img.shields.io/github/v/release/YOUR_USERNAME/usb-serial-android)](https://github.com/YOUR_USERNAME/usb-serial-android/releases)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

Um aplicativo Android nativo para comunicação USB-Serial com dispositivos como Arduino, ESP32, conversores USB-Serial e outros dispositivos compatíveis.

## ✨ Funcionalidades

- 🔌 **Detecção automática** de dispositivos USB-Serial
- 📡 **Comunicação bidirecional** com dispositivos
- 🖥️ **Terminal em tempo real** com dados TX/RX
- ⚙️ **Suporte múltiplos chipsets**:
  - Arduino (VID: 0x2341)
  - CH340/CH341 (VID: 0x1A86) 
  - FTDI (VID: 0x0403)
  - Silicon Labs CP210x (VID: 0x10C4)
  - Prolific PL2303 (VID: 0x067B)
  - CDC Communication devices
- 🛠️ **Interface limpa e intuitiva**
- 📝 **Logs detalhados** para diagnóstico
- 🔄 **Conexão/desconexão manual**
- 📤 **Envio de comandos customizados**

## 📱 Download

### APK Compilado Automaticamente

Os APKs são compilados automaticamente via GitHub Actions:

1. Vá para [Releases](https://github.com/YOUR_USERNAME/usb-serial-android/releases)
2. Baixe a versão mais recente:
   - **Debug APK**: Para desenvolvimento e testes
   - **Release APK**: Para uso em produção

### Compilar Localmente

```bash
git clone https://github.com/YOUR_USERNAME/usb-serial-android.git
cd usb-serial-android
./gradlew assembleDebug
```

## 🚀 Como Usar

1. **Instalar o APK** no dispositivo Android
2. **Conectar dispositivo USB** via cabo OTG
3. **Abrir o app** e selecionar dispositivo
4. **Conceder permissão USB** quando solicitado
5. **Conectar** e começar a comunicação!

## 📸 Screenshots

### Interface Principal
```
┌─────────────────────────────────────┐
│ Status: ✅ Conectado                │
├─────────────────────────────────────┤
│ Dispositivos USB:                   │
│ ┌─────────────────────────────────┐ │
│ │ Arduino Uno (VID:2341)      [🔄]│ │
│ └─────────────────────────────────┘ │
│                                     │
│ [Conectar]         [Desconectar]    │
│                                     │
│ Enviar Comando:                     │
│ ┌─────────────────────────────────┐ │
│ │ AT                          │📤││ │
│ └─────────────────────────────────┘ │
│                                     │
│ Terminal de Comunicação:    [Limpar]│
│ ┌─────────────────────────────────┐ │
│ │ [16:30:15] TX: AT              │ │
│ │ [16:30:15] RX: OK              │ │
│ │ [16:30:20] TX: AT+VERSION      │ │
│ │ [16:30:20] RX: ESP32_v1.0.4    │ │
│ └─────────────────────────────────┘ │
└─────────────────────────────────────┘
```

## 🛠️ Requisitos

- **Android 7.0** (API level 24) ou superior
- **Suporte USB Host** (maioria dos dispositivos Android)
- **Cabo USB OTG** para conectar dispositivos

## 🔧 Dispositivos Testados

| Dispositivo | Chipset | Status |
|-------------|---------|--------|
| Arduino Uno | ATmega328P | ✅ |
| Arduino Nano | CH340 | ✅ |
| ESP32 DevKit | CP2102 | ✅ |
| ESP8266 NodeMCU | CH340 | ✅ |
| Conversor FTDI | FT232RL | ✅ |

## 📚 Arquitetura

### Componentes Principais

- **`UsbSerialManager.kt`**: Gerenciamento da comunicação USB
- **`MainActivity.kt`**: Interface principal do usuário
- **`activity_main.xml`**: Layout da interface
- **`device_filter.xml`**: Filtros para dispositivos USB

### Fluxo de Comunicação

```
[Dispositivo USB] ↔ [UsbSerialManager] ↔ [MainActivity] ↔ [Interface do Usuário]
```

## 🔧 Configuração para Desenvolvimento

### Pré-requisitos

- Android Studio 4.0+
- JDK 11+
- Android SDK 34

### Setup

1. Clone o repositório
2. Abra no Android Studio
3. Sync do projeto
4. Build e instalar no dispositivo

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. Fork o projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## 📝 Licença

Este projeto está licenciado sob a MIT License - veja o arquivo [LICENSE](LICENSE) para detalhes.

## 🙏 Agradecimentos

- Comunidade Android Open Source
- Desenvolvedores de bibliotecas USB-Serial
- Contribuidores do projeto

## 📞 Suporte

- 🐛 **Bugs**: [Issues](https://github.com/YOUR_USERNAME/usb-serial-android/issues)
- 💡 **Feature Requests**: [Issues](https://github.com/YOUR_USERNAME/usb-serial-android/issues)
- 📧 **Email**: seu@email.com

---

⭐ **Se este projeto te ajudou, considere dar uma estrela!** ⭐ 