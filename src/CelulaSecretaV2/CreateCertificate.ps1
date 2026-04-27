# Script para criar certificado auto-assinado para Code Signing
# Execute como Administrador!

param(
    [string]$CertPassword = "CelulaSecreta@2026"
)

Write-Host "=== Criando Certificado de Code Signing ===" -ForegroundColor Green
Write-Host ""

# Verificar se está como Administrador
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")

if (-not $isAdmin) {
    Write-Host "❌ ERRO: Este script deve ser executado como Administrador!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Instruções:" -ForegroundColor Cyan
    Write-Host "  1. Procure por 'PowerShell'" -ForegroundColor White
    Write-Host "  2. Clique direito → 'Executar como administrador'" -ForegroundColor White
    Write-Host "  3. Execute novamente este script" -ForegroundColor White
    exit 1
}

Write-Host "✓ Executando como Administrador" -ForegroundColor Green
Write-Host ""

# Criar diretório para certificados
$certDir = "C:\Certs"
if (-not (Test-Path $certDir)) {
    Write-Host "Criando diretório de certificados: $certDir"
    mkdir -Force $certDir | Out-Null
}

# Verificar se já existe certificado
$existingCert = Get-ChildItem Cert:\CurrentUser\My -CodeSigningCert | Where-Object { $_.Subject -match "CelulaSecreta" }

if ($existingCert) {
    Write-Host "⚠️  Certificado existente encontrado!" -ForegroundColor Yellow
    Write-Host "  Subject: $($existingCert.Subject)" -ForegroundColor Cyan
    Write-Host "  Thumbprint: $($existingCert.Thumbprint)" -ForegroundColor Cyan
    Write-Host ""

    $response = Read-Host "Deseja criar um novo certificado? (S/N)"
    if ($response -ne "S" -and $response -ne "s") {
        Write-Host "Operação cancelada." -ForegroundColor Yellow
        exit 0
    }
}

Write-Host "Passo 1: Criando certificado auto-assinado..." -ForegroundColor Cyan

try {
    $cert = New-SelfSignedCertificate `
        -CertStoreLocation Cert:\CurrentUser\My `
        -DnsName "CelulaSecreta" `
        -Subject "CN=CelulaSecreta, O=JotaPe, L=Sao Paulo, C=BR" `
        -NotAfter (Get-Date).AddYears(5) `
        -KeyUsage DigitalSignature `
        -Type CodeSigningCert `
        -KeyExportPolicy Exportable `
        -FriendlyName "CelulaSecreta Code Signing Certificate"

    Write-Host "✓ Certificado criado!" -ForegroundColor Green
    Write-Host "  Thumbprint: $($cert.Thumbprint)" -ForegroundColor Cyan
    Write-Host "  Válido até: $($cert.NotAfter)" -ForegroundColor Cyan
    Write-Host ""

} catch {
    Write-Host "❌ Erro ao criar certificado: $_" -ForegroundColor Red
    exit 1
}

Write-Host "Passo 2: Exportando certificado para arquivo PFX..." -ForegroundColor Cyan

try {
    $securePassword = ConvertTo-SecureString -String $CertPassword -AsPlainText -Force

    Export-PfxCertificate `
        -Cert $cert `
        -FilePath "$certDir\CelulaSecreta.pfx" `
        -Password $securePassword `
        -Force | Out-Null

    Write-Host "✓ Certificado exportado!" -ForegroundColor Green
    Write-Host "  Arquivo: $certDir\CelulaSecreta.pfx" -ForegroundColor Cyan
    Write-Host ""

} catch {
    Write-Host "❌ Erro ao exportar certificado: $_" -ForegroundColor Red
    exit 1
}

Write-Host "Passo 3: Instalando certificado na loja confiável..." -ForegroundColor Cyan

try {
    # Copiar para loja confiável
    $securePassword = ConvertTo-SecureString -String $CertPassword -AsPlainText -Force

    Import-PfxCertificate `
        -FilePath "$certDir\CelulaSecreta.pfx" `
        -CertStoreLocation Cert:\CurrentUser\Root `
        -Password $securePassword `
        -Exportable -Force | Out-Null

    Write-Host "✓ Certificado instalado na loja confiável!" -ForegroundColor Green
    Write-Host ""

} catch {
    Write-Host "⚠️  Aviso ao instalar na loja: $_" -ForegroundColor Yellow
    Write-Host ""
}

Write-Host "=== ✓ Certificado criado com sucesso! ===" -ForegroundColor Green
Write-Host ""
Write-Host "Próximo passo: Assinar o instalador" -ForegroundColor Cyan
Write-Host ""
Write-Host "Execute o seguinte comando para gerar o instalador assinado:" -ForegroundColor White
Write-Host ""
Write-Host "  .\build-installer.ps1 -CertificatePath 'C:\Certs\CelulaSecreta.pfx' -CertificatePassword '$CertPassword'" -ForegroundColor Green
Write-Host ""
Write-Host "Informações importantes:" -ForegroundColor Yellow
Write-Host "  • Senha: $CertPassword" -ForegroundColor White
Write-Host "  • Arquivo: C:\Certs\CelulaSecreta.pfx" -ForegroundColor White
Write-Host "  • Válido por: 5 anos" -ForegroundColor White
Write-Host "  • Tipo: Code Signing Certificate" -ForegroundColor White
Write-Host ""
Write-Host "⚠️  IMPORTANTE:" -ForegroundColor Yellow
Write-Host "  • Guarde a senha em um local seguro!" -ForegroundColor White
Write-Host "  • Este certificado é auto-assinado (confível localmente)" -ForegroundColor White
Write-Host "  • Para distribuição pública, obtenha certificado comercial" -ForegroundColor White
Write-Host ""

