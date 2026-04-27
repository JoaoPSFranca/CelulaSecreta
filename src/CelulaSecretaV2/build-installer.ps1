# Script de Build para CelulaSecreta com melhorias de segurança
# Este script melhora a compatibilidade com Windows Firewall Inteligente

param(
    [string]$CertificatePath = "",
    [string]$CertificatePassword = "",
    [string]$DestinationPath = "D:\Bolsa-Executavel\CelulaSecreta-Windows",
    [string]$IconPath = "D:\PortableGit\Projects\CelulaSecreta\img\core\icon_filled.ico"
)

Write-Host "=== CelulaSecreta Build Script ===" -ForegroundColor Green
Write-Host ""

# Step 1: Compilar e empacotar
Write-Host "Step 1: Compilando o projeto Maven..." -ForegroundColor Cyan
mvn clean package javafx:jlink

if ($LASTEXITCODE -ne 0) {
    Write-Host "Erro na compilação Maven!" -ForegroundColor Red
    exit 1
}

Write-Host "Compilação concluída com sucesso!" -ForegroundColor Green
Write-Host ""

# Step 2: Gerar imagem jpackage
Write-Host "Step 2: Gerando imagem jpackage..." -ForegroundColor Cyan
$jpackagePath = Get-Command jpackage -ErrorAction SilentlyContinue
if (-not $jpackagePath) {
    Write-Host "jpackage não encontrado no PATH" -ForegroundColor Red
    exit 1
}

# Step 3: Gerar instalador EXE com jpackage
Write-Host "Step 3: Gerando instalador EXE..." -ForegroundColor Cyan

& "$env:JAVA_HOME\bin\jpackage.exe" `
   --type exe `
   --input target `
   --main-jar CelulaSecreta-1.0.0.jar `
   --main-class br.edu.ifsp.pep.App `
   --name CelulaSecreta `
   --app-version 4.2.0 `
   --vendor "JotaPe" `
   --runtime-image target\app `
   --icon "$IconPath" `
   --win-shortcut `
   --win-dir-chooser `
   --win-per-user-install `
   --win-console false `
   --win-menu `
   --dest "$DestinationPath" `
   --verbose

if ($LASTEXITCODE -ne 0) {
    Write-Host "Erro na geração do instalador!" -ForegroundColor Red
    exit 1
}

Write-Host "Instalador gerado com sucesso!" -ForegroundColor Green
Write-Host ""

# Step 4: Assinar código (opcional)
if ($CertificatePath -and (Test-Path $CertificatePath)) {
    Write-Host "Step 4: Assinando o executável..." -ForegroundColor Cyan

    $signtoolPath = "C:\Program Files (x86)\Windows Kits\10\bin\10.0.22621.0\x64\signtool.exe"

    if (Test-Path $signtoolPath) {
        $exePath = Get-ChildItem -Path $DestinationPath -Filter "*.exe" -Recurse | Select-Object -First 1

        if ($exePath) {
            & "$signtoolPath" sign `
                /f "$CertificatePath" `
                /p "$CertificatePassword" `
                /t "http://timestamp.sectigo.com" `
                /fd sha256 `
                "$($exePath.FullName)"

            if ($LASTEXITCODE -eq 0) {
                Write-Host "Executável assinado com sucesso!" -ForegroundColor Green
            } else {
                Write-Host "Aviso: Falha ao assinar o executável" -ForegroundColor Yellow
            }
        }
    } else {
        Write-Host "signtool.exe não encontrado. Pulando assinatura." -ForegroundColor Yellow
    }
} else {
    Write-Host "Step 4: Assinatura não configurada (opcional)" -ForegroundColor Yellow
    Write-Host "Para assinar o executável, execute com parâmetros:" -ForegroundColor Yellow
    Write-Host ".\build-installer.ps1 -CertificatePath 'C:\seu_certificado.pfx' -CertificatePassword 'sua_senha'" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== Build concluído com sucesso! ===" -ForegroundColor Green
Write-Host "Instalador gerado em: $DestinationPath" -ForegroundColor Cyan
Write-Host ""
Write-Host "DICAS para melhorar compatibilidade com Firewall Inteligente:" -ForegroundColor Yellow
Write-Host "1. Code Signing: Obtenha um certificado e use o parâmetro -CertificatePath" -ForegroundColor White
Write-Host "2. Reputação: Distribua o instalador através de canais oficiais" -ForegroundColor White
Write-Host "3. Manifesto: Verifique que o manifesto está correto" -ForegroundColor White

