# ⚡ Quick Start: Resolver SmartScreen em 5 Minutos

## TL;DR (Resumo Executivo)

```powershell
# 1. Como Administrador:
cd "D:\PortableGit\Projects\CelulaSecreta\src\CelulaSecretaV2"

# 2. Criar certificado (uma vez):
.\CreateCertificate.ps1

# 3. Gerar instalador assinado:
.\build-installer.ps1 `
    -CertificatePath "C:\Certs\CelulaSecreta.pfx" `
    -CertificatePassword "CelulaSecreta@2026"

# 4. Instalar:
# Duplo clique em: D:\Bolsa-Executavel\CelulaSecreta-Windows\main.msi

# Resultado: ✅ Sem avisos do SmartScreen!
```

---

## Passo-a-Passo

### 1️⃣ Abrir PowerShell como Admin (1 min)

```
Windows + X → Windows Terminal (Admin)
OU
Procure "PowerShell" → Clique direito → "Executar como administrador"
```

### 2️⃣ Criar Certificado (2 min)

```powershell
cd "D:\PortableGit\Projects\CelulaSecreta\src\CelulaSecretaV2"
.\CreateCertificate.ps1

# Verá:
# ✓ Certificado criado!
# ✓ Certificado exportado!
# ✓ Certificado instalado!
```

### 3️⃣ Gerar Instalador (10 min - tempo de build)

```powershell
.\build-installer.ps1 `
    -CertificatePath "C:\Certs\CelulaSecreta.pfx" `
    -CertificatePassword "CelulaSecreta@2026"

# Verá:
# Step 4: Assinando o instalador...
#   ✓ MSI assinado com sucesso!
#   ✓ EXE assinado com sucesso!
```

### 4️⃣ Testar Instalador (5 min)

```
1. Abra Explorer
2. Navegue: D:\Bolsa-Executavel\CelulaSecreta-Windows\
3. Duplo clique: main.msi
4. Resultado: ✅ Instala sem avisos!
```

---

## Resultado Esperado

### ANTES
```
❌ "main.msi foi bloqueado pois não é possível identificar quem publicou"
```

### DEPOIS
```
✅ Instala normalmente
✅ Sem avisos do SmartScreen
✅ Atalho criado no menu iniciar
```

---

## Próximas Vezes

Agora que o certificado foi criado, para novos builds:

```powershell
# Só precisa disso (o certificado já existe):
.\build-installer.ps1 `
    -CertificatePath "C:\Certs\CelulaSecreta.pfx" `
    -CertificatePassword "CelulaSecreta@2026"
```

---

## Troubleshooting Rápido

| Erro | Solução |
|------|---------|
| "Not running as Admin" | Abra PowerShell como Administrador |
| "ExecutionPolicy" | `Set-ExecutionPolicy Bypass -Scope CurrentUser` |
| "signtool not found" | Instale Windows SDK |
| "Certificado já existe" | Script pergunta se quer criar novo |

---

## Informações do Certificado

```
Arquivo: C:\Certs\CelulaSecreta.pfx
Senha: CelulaSecreta@2026
Válido por: 5 anos
Tipo: Code Signing
```

✅ **Pronto para usar!**


