# 🔐 Solução: Windows SmartScreen Bloqueando Instalador MSI

## Problema Exato

```
❌ "main.msi foi bloqueado pois não é possível identificar quem publicou 
   e não é um aplicativo com o qual estamos familiarizados"
```

Isso significa: **O arquivo .msi não está assinado digitalmente**

---

## Causa Raiz

Quando você usa `jpackage` para gerar um Windows Installer (.msi), o arquivo resultante **não está assinado**. O Windows SmartScreen desconfia de:
- ✗ Executáveis não assinados
- ✗ Instaladores não assinados (MSI, EXE)
- ✗ Aplicações de origem desconhecida

---

## Solução Rápida (5 minutos)

### Passo 1: Criar Certificado Auto-Assinado

Abra **PowerShell como Administrador** e execute:

```powershell
# Navegue até o projeto
cd "D:\PortableGit\Projects\CelulaSecreta\src\CelulaSecretaV2"

# Execute o script de criação
.\CreateCertificate.ps1
```

**O que acontece:**
```
✓ Certificado criado: CN=CelulaSecreta
✓ Arquivo salvo em: C:\Certs\CelulaSecreta.pfx
✓ Senha: CelulaSecreta@2026
```

### Passo 2: Assinar o Instalador

```powershell
# Gerar instalador COM assinatura
.\build-installer.ps1 `
    -CertificatePath "C:\Certs\CelulaSecreta.pfx" `
    -CertificatePassword "CelulaSecreta@2026"
```

**O que acontece:**
```
Step 1: Compilando o projeto Maven...
Step 2: Gerando imagem jpackage...
Step 3: Gerando instalador EXE...
Step 4: Assinando o instalador...
  ✓ MSI assinado com sucesso!
  ✓ EXE assinado com sucesso!
```

### Passo 3: Testar o Instalador

```
1. Navegue até: D:\Bolsa-Executavel\CelulaSecreta-Windows\
2. Duplo clique em: main.msi (ou CelulaSecreta-Installer.msi)
3. ESPERADO: Nenhum aviso do SmartScreen!
4. ✓ Instalação procede normalmente
```

---

## Comparação: Antes vs Depois

### ANTES (Bloqueado)

```
[Usuário duplo-clica em .msi]
         ↓
⚠️ Windows SmartScreen detecta
         ↓
"Arquivo bloqueado. Publicador desconhecido"
         ↓
❌ Instalação impedida
```

### DEPOIS (Assinado)

```
[Usuário duplo-clica em .msi]
         ↓
✅ Windows valida assinatura
         ↓
"Arquivo verificado: CelulaSecreta"
         ↓
✅ Instalação procede normalmente
```

---

## Passo-a-Passo Detalhado

### 1️⃣ Abrir PowerShell como Administrador

```
Windows 10/11:
  Clique em Windows + X
  └─ Escolha: Windows Terminal (Admin)
  └─ OU: PowerShell (Admin)
  
OU:
  Procure por "PowerShell"
  └─ Clique direito
  └─ "Executar como administrador"
```

### 2️⃣ Executar CreateCertificate.ps1

```powershell
cd "D:\PortableGit\Projects\CelulaSecreta\src\CelulaSecretaV2"
.\CreateCertificate.ps1

# Saída esperada:
# ✓ Certificado criado!
# ✓ Certificado exportado!
# ✓ Certificado instalado!
```

### 3️⃣ Gerar Instalador Assinado

```powershell
.\build-installer.ps1 `
    -CertificatePath "C:\Certs\CelulaSecreta.pfx" `
    -CertificatePassword "CelulaSecreta@2026"

# Saída esperada:
# Step 4: Assinando o instalador...
#   ✓ MSI assinado com sucesso!
#   ✓ EXE assinado com sucesso!
```

### 4️⃣ Verificar Assinatura

```powershell
# Verifique se o MSI foi assinado corretamente
$msiPath = "D:\Bolsa-Executavel\CelulaSecreta-Windows\main.msi"
Get-AuthenticodeSignature -FilePath $msiPath

# Resultado esperado:
# Status: Valid
# SignerCertificate: CN=CelulaSecreta
```

---

## Opção 2: Instalar Certificado Localmente

Se você tiver criado o certificado e quer instalar em outro computador:

### No Computador com Certificado (SERVIDOR)

```powershell
# Copie o arquivo PFX para compartilhamento seguro
Copy-Item "C:\Certs\CelulaSecreta.pfx" "D:\Compartilhamento\CelulaSecreta.pfx"
```

### No Computador do Usuário (CLIENTE)

```powershell
# Como Administrador
$pwd = ConvertTo-SecureString "CelulaSecreta@2026" -AsPlainText -Force

Import-PfxCertificate `
    -FilePath "D:\Compartilhamento\CelulaSecreta.pfx" `
    -CertStoreLocation Cert:\LocalMachine\Root `
    -Password $pwd

# Agora o SmartScreen confia no instalador!
```

---

## Opção 3: Certificado Comercial (Para Distribuição Pública)

Se vai distribuir publicamente para muitos usuários:

### Fornecedores Recomendados

| Fornecedor | Custo | Tempo | Confiança |
|------------|-------|-------|-----------|
| **Sectigo** | $199/ano | 2-24h | 100% |
| **DigiCert** | $299/ano | 1-24h | 100% |
| **GlobalSign** | $299/ano | 2-24h | 100% |

### Processo

1. **Compre certificado** em uma das CAs acima
2. **Receba arquivo .pfx** por email
3. **Use no build:**
   ```powershell
   .\build-installer.ps1 `
       -CertificatePath "C:\Certs\SeuCertificadoComercial.pfx" `
       -CertificatePassword "SuaSenha"
   ```
4. **Resultado**: 100% compatibilidade, sem avisos SmartScreen

---

## Entendendo os Certificados

### Auto-Assinado (Gratuito)
```
✅ Grátis
✅ Rápido (2 minutos)
✅ Funciona localmente
✅ Pode ser instalado em máquinas amigas
❌ Não confiável globalmente
❌ Usuários veem "Aviso: Publicador desconhecido" na primeira vez
```

### Comercial ($200-500/ano)
```
✅ Confiável globalmente
✅ Sem avisos SmartScreen
✅ Microsoft reconhece automaticamente
✅ Timestamp válido por vários anos
✅ Suporte profissional
❌ Custo: $200-500 por ano
```

---

## Troubleshooting

### Erro: "signtool.exe não encontrado"

```powershell
❌ Problema: Windows SDK não instalado

✅ Solução:
   1. Visite: https://developer.microsoft.com/windows/downloads/windows-sdk/
   2. Baixe "Windows SDK"
   3. Instale, selecionando "Signing Tools for Windows"
   4. Reinicie PowerShell
   5. Tente novamente
```

### Erro: "ExecutionPolicy"

```powershell
❌ Erro: "PowerShell scripts are disabled"

✅ Solução:
   Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope CurrentUser
```

### Erro: "Not running as Administrator"

```powershell
❌ Problema: CreateCertificate.ps1 precisa ser admin

✅ Solução:
   1. Feche PowerShell
   2. Procure por "PowerShell"
   3. Clique direito → "Executar como administrador"
   4. Tente novamente
```

### Ainda vejo aviso do SmartScreen?

```
Se ainda vê aviso mesmo com certificado auto-assinado:

1. ✓ Instale o certificado na loja confiável do usuário
2. ✓ Aguarde 24-48 horas (Microsoft aprende a reputação)
3. ✓ Use certificado comercial (solução garantida)
4. ✓ Distribua via Microsoft Store (confiança automática)
```

---

## Verificar Assinatura do MSI

### Windows Explorer

```
1. Navegue até: D:\Bolsa-Executavel\CelulaSecreta-Windows\
2. Clique direito em: main.msi
3. Selecione: Propriedades
4. Vá para aba: Digital Signatures
5. ✓ Deve listar: CN=CelulaSecreta
```

### PowerShell

```powershell
$msiPath = "D:\Bolsa-Executavel\CelulaSecreta-Windows\main.msi"
Get-AuthenticodeSignature $msiPath

# Saída esperada:
# Status        : Valid
# Issuer        : CN=CelulaSecreta, O=JotaPe
# Subject       : CN=CelulaSecreta, O=JotaPe
# NotBefore     : 01/01/2026 10:00:00 AM
# NotAfter      : 01/01/2031 10:00:00 AM
```

---

## Próximos Passos

### Imediato (Hoje)
```
1. ✓ Execute CreateCertificate.ps1
2. ✓ Execute build-installer.ps1 com certificado
3. ✓ Teste o instalador
```

### Curto Prazo (Esta Semana)
```
1. ✓ Distribua para teste interno
2. ✓ Verifique com usuários que não têm cert instalado
3. ✓ Documente para novo build
```

### Longo Prazo (Se Distribuição Pública)
```
1. Obtenha certificado comercial
2. Configure CI/CD para assinar automaticamente
3. Distribua via Microsoft Store (opcional)
```

---

## Resumo

| Passo | Comando | Tempo |
|-------|---------|-------|
| 1. Criar Cert | `.\CreateCertificate.ps1` | 2 min |
| 2. Build com Sign | `.\build-installer.ps1 -Cert...` | 10 min |
| 3. Instalar | Duplo clique em .msi | 5 min |
| 4. Verificar | Sem avisos SmartScreen | ✓ |

**Total: ~20 minutos**

---

## Status

✅ **Problema**: MSI bloqueado pelo SmartScreen  
✅ **Solução**: Assinar com certificado auto-assinado  
✅ **Resultado**: Instalador confiável localmente  

Se precisar de distribuição pública, upgrade para certificado comercial.


