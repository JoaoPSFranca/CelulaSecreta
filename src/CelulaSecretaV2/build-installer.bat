@echo off
REM Script de Build para CelulaSecreta - Versão Batch
REM Para máxima compatibilidade com PowerShell, execute assim:
REM powershell -ExecutionPolicy Bypass -File build-installer.ps1

setlocal enabledelayedexpansion

echo.
echo ==================== CelulaSecreta Build ====================
echo.

REM Verificar se Maven está disponível
where mvn >nul 2>nul
if errorlevel 1 (
    echo [ERRO] Maven não encontrado no PATH
    echo Por favor, instale Maven ou adicione-o ao PATH
    exit /b 1
)

echo [1/3] Compilando projeto Maven...
call mvn clean package javafx:jlink
if errorlevel 1 (
    echo [ERRO] Falha na compilação Maven
    exit /b 1
)
echo [OK] Compilação concluída

echo.
echo [2/3] Gerando instalador EXE...

REM Detectar JAVA_HOME
if not defined JAVA_HOME (
    echo [ERRO] JAVA_HOME não definido. Configure a variável de ambiente.
    exit /b 1
)

set JPACKAGE="%JAVA_HOME%\bin\jpackage.exe"
if not exist %JPACKAGE% (
    echo [ERRO] jpackage.exe não encontrado em %JAVA_HOME%\bin
    exit /b 1
)

set ICON_PATH=D:\PortableGit\Projects\CelulaSecreta\img\core\icon_filled.ico
set DEST_PATH=D:\Bolsa-Executavel\CelulaSecreta-Windows

if not exist "!ICON_PATH!" (
    echo [AVISO] Ícone não encontrado: !ICON_PATH!
    echo Continuando sem ícone...
    set JPACKAGE_CMD=%JPACKAGE% --type exe --input target --main-jar CelulaSecreta-1.0.0.jar --main-class br.edu.ifsp.pep.App --name CelulaSecreta --app-version 4.2.0 --vendor "JotaPe" --runtime-image target\app --win-shortcut --win-dir-chooser --win-per-user-install --dest "!DEST_PATH!" --verbose
) else (
    set JPACKAGE_CMD=%JPACKAGE% --type exe --input target --main-jar CelulaSecreta-1.0.0.jar --main-class br.edu.ifsp.pep.App --name CelulaSecreta --app-version 4.2.0 --vendor "JotaPe" --runtime-image target\app --icon "!ICON_PATH!" --win-shortcut --win-dir-chooser --win-per-user-install --dest "!DEST_PATH!" --verbose
)

%JPACKAGE_CMD%
if errorlevel 1 (
    echo [ERRO] Falha na geração do instalador
    exit /b 1
)
echo [OK] Instalador gerado com sucesso

echo.
echo [3/3] Informações de Segurança
echo ===============================================================
echo.
echo Para melhorar a compatibilidade com Windows Firewall Inteligente:
echo.
echo OPÇÃO 1: Code Signing (RECOMENDADO)
echo   - Use o script PowerShell: .\build-installer.ps1
echo   - Configure com certificado digital
echo.
echo OPÇÃO 2: Distribuição
echo   - Distribua através de canais oficiais
echo   - Solicite review no Windows App Store
echo.
echo Leia o arquivo FIREWALL_SOLUTION.md para mais detalhes.
echo.
echo ===============================================================
echo [OK] Build concluído com sucesso!
echo Instalador em: !DEST_PATH!
echo ===============================================================
echo.

endlocal

