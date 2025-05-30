#!/bin/bash

echo "📦 Installing spring-cli..."

URL_BASE="https://raw.githubusercontent.com/Narendra-Reddy1/spring-cli/master"

# Detect the operating system
OS_TYPE=$(uname -s)

# Set INSTALL_DIR based on OS
if [[ "$OS_TYPE" == "Linux" || "$OS_TYPE" == "Darwin" ]]; then
    # Unix-like systems (Linux, macOS)
    INSTALL_DIR="/usr/local/bin"
    USE_SUDO="sudo"
elif [[ "$OS_TYPE" == "CYGWIN"* || "$OS_TYPE" == "MINGW"* ]]; then
    # Windows (Git Bash, Cygwin)
    INSTALL_DIR="$HOME/spring-cli/bin"
    USE_SUDO=""
else
    echo "Error: Unsupported operating system: $OS_TYPE"
    exit 1
fi

# Check for Java
if ! command -v java >/dev/null 2>&1; then
    echo "Error: Java is not installed or not in PATH."
    exit 1
fi

# Check for sudo privileges if needed
if [[ -n "$USE_SUDO" ]]; then
    if ! $USE_SUDO -n true 2>/dev/null; then
        echo "Error: This script requires sudo privileges to install to $INSTALL_DIR."
        exit 1
    fi
fi

# Check if INSTALL_DIR exists, create it only if it doesn't
if [[ ! -d "$INSTALL_DIR" ]]; then
    mkdir -p "$INSTALL_DIR" || { echo "Error: Failed to create directory $INSTALL_DIR"; exit 1; }
fi
# Clean up temporary files on exit
trap 'rm -f spring-cli spring-cli.bat spring-cli.jar' EXIT

# Download the wrapper script
curl -sSL "$URL_BASE/spring-cli.sh" -o spring-cli || { echo "Error: Failed to download spring-cli.sh"; exit 1; }

# Download the JAR
curl -sSL "$URL_BASE/spring-cli.jar" -o spring-cli.jar || { echo "Error: Failed to download spring-cli.jar"; exit 1; }

# Move them into a CLI directory
mkdir -p ~/.spring-cli || { echo "Error: Failed to create directory ~/.spring-cli"; exit 1; }
mv spring-cli ~/.spring-cli/
mv spring-cli.jar ~/.spring-cli/

# Create a wrapper script based on OS
if [[ "$OS_TYPE" == "Linux" || "$OS_TYPE" == "Darwin" ]]; then
    # Unix-like systems: Create a Bash wrapper
    echo '#!/bin/bash' > spring-cli
    echo 'java -jar ~/.spring-cli/spring-cli.jar "$@"' >> spring-cli
    chmod +x spring-cli
elif [[ "$OS_TYPE" == "CYGWIN"* || "$OS_TYPE" == "MINGW"* ]]; then
    # Windows: Create a Batch wrapper
    echo '@echo off' > spring-cli.bat
    echo 'java -jar %USERPROFILE%\.spring-cli\spring-cli.jar %*' >> spring-cli.bat
fi

# Move the wrapper to INSTALL_DIR
if [[ "$OS_TYPE" == "Linux" || "$OS_TYPE" == "Darwin" ]]; then
    $USE_SUDO mv spring-cli "$INSTALL_DIR/spring-cli" || { echo "Error: Failed to move spring-cli to $INSTALL_DIR"; exit 1; }
elif [[ "$OS_TYPE" == "CYGWIN"* || "$OS_TYPE" == "MINGW"* ]]; then
    mv spring-cli.bat "$INSTALL_DIR/spring-cli.bat" || { echo "Error: Failed to move spring-cli.bat to $INSTALL_DIR"; exit 1; }
fi

# Add INSTALL_DIR to PATH if not already present
if [[ "$OS_TYPE" == "Linux" || "$OS_TYPE" == "Darwin" || "$OS_TYPE" == "CYGWIN"* || "$OS_TYPE" == "MINGW"* ]]; then
    # Unix-like systems (including Git Bash)
    if [[ ":$PATH:" != *":$INSTALL_DIR:"* ]]; then
        echo "Adding $INSTALL_DIR to PATH..."
        SHELL_CONFIG="$HOME/.bashrc"
        if [[ "$SHELL" == *"zsh"* ]]; then
            SHELL_CONFIG="$HOME/.zshrc"
        fi
        echo "export PATH=\$PATH:$INSTALL_DIR" >> "$SHELL_CONFIG"
        echo "Note: Run 'source $SHELL_CONFIG' or restart your terminal to update your PATH."
    fi
fi

# On Windows, also update the system PATH for PowerShell/Command Prompt
if [[ "$OS_TYPE" == "CYGWIN"* || "$OS_TYPE" == "MINGW"* ]]; then
    powershell -Command "
        \$path = [Environment]::GetEnvironmentVariable('Path', 'User');
        if (-not \$path.Contains('$INSTALL_DIR')) {
            Write-Host 'Adding $INSTALL_DIR to PATH...';
            \$newPath = \"\$path;$INSTALL_DIR\";
            [Environment]::SetEnvironmentVariable('Path', \$newPath, 'User');
            Write-Host 'Note: You may need to restart your terminal to update your PATH.';
        }
    "
fi

echo "✅ Done. Run: spring-cli install"