#!/bin/bash

echo "🚀 Preparando deploy para GitHub..."

# Initialize git if not already initialized
if [ ! -d ".git" ]; then
    echo "📁 Inicializando repositório Git..."
    git init
    git branch -M main
fi

# Add all files
echo "📦 Adicionando arquivos..."
git add .

# Commit with current timestamp
echo "💾 Fazendo commit..."
git commit -m "🔧 USB Serial App - Complete implementation with GitHub Actions

✅ Features implemented:
- USB-Serial communication (Arduino, CH340, FTDI, CP210x, Prolific)
- Real-time terminal interface
- Manual device connection/disconnection
- Custom command sending
- Auto AT command sending every 15s
- Comprehensive device detection and diagnostics
- Professional UI with connection status indicators

🏗️ Build system:
- GitHub Actions automatic APK compilation
- Java 11+ compatibility
- Modern Android SDK (API 34)
- Gradle 8.5 with caching optimization

📱 Ready for production use!"

echo "✅ Commit realizado!"
echo ""
echo "🌐 Para fazer deploy:"
echo "1. Crie um repositório no GitHub"
echo "2. Execute:"
echo "   git remote add origin https://github.com/SEU_USUARIO/usb-serial-android.git"
echo "   git push -u origin main"
echo ""
echo "🔧 Após o push, o GitHub Actions irá:"
echo "- Compilar automaticamente o APK"
echo "- Criar release com debug e release APKs"
echo "- Disponibilizar downloads em Releases"
echo ""
echo "�� Pronto para usar!" 