# Sistema de Código de Sala - Célula Secreta

## 📋 Visão Geral

O sistema de código de sala permite que jogadores se conectem usando apenas um **código de 6 caracteres** (ex: `AB3X7K`), sem necessidade de digitar endereços IP. O IP do host é descoberto automaticamente através de broadcast na rede local.

## 🎯 Como Funciona

### 1. **Host Cria uma Sala**
- O host clica em "Criar Sala"
- Um código único é gerado automaticamente (ex: `AB3X7K`)
- O código é exibido em um diálogo
- O host começa a transmitir seu IP na rede com este código

### 2. **Cliente Entra na Sala**
- O cliente clica em "Entrar em Sala"
- Digite apenas o código (`AB3X7K`)
- O sistema procura automaticamente pelo host na rede local
- A conexão é estabelecida automaticamente

## 📊 Formato do Código

- **Comprimento**: 6 caracteres
- **Caracteres permitidos**: A-Z (maiúsculas) e 0-9
- **Exemplos**: `AB3X7K`, `Z9M2Q5`, `K7N1P8`

## 🔧 Implementação Técnica

### Classes Principais:

#### 1. **RoomCodeGenerator**
Gera códigos alfanuméricos aleatórios de 6 caracteres.

```java
String code = RoomCodeGenerator.generateRoomCode();
// Resultado: "AB3X7K"
```

#### 2. **RoomServer**
Gerencia o broadcast e descoberta de salas na rede local.

```java
// Host transmite sua sala
RoomServer.startBroadcastingRoom("AB3X7K", "192.168.0.100");

// Cliente procura o host usando o código
String hostIp = RoomServer.discoverHostByCode("AB3X7K");
// Resultado: "192.168.0.100"
```

### Fluxo de Rede

1. **Host** gera código e começa a transmitir: `AB3X7K:192.168.0.100` via UDP broadcast (porta 54322)
2. **Cliente** liga o receptor e fica ouvindo broadcasts
3. **Cliente** valida o código recebido
4. **Cliente** recupera o IP e conecta via TCP (porta 54321)

## ✅ Benefícios

- ✅ **Simplicidade**: Apenas 6 caracteres para digitar
- ✅ **Automático**: Não precisa descobrir ou digitar IP
- ✅ **Intuitivo**: Interface clara e fácil de usar
- ✅ **Funciona em rede local**: Usa broadcast UDP para descoberta

## 📝 Exemplo de Uso

**Host:**
1. Clica em "Multiplayer"
2. Clica em "Criar Sala"
3. Recebe código: `K7N1P8`
4. Compartilha código com o outro jogador

**Cliente:**
1. Clica em "Multiplayer"
2. Clica em "Entrar em Sala"
3. Digita: `K7N1P8`
4. Conecta automaticamente ao host

## ⚙️ Configurações Técnicas

- **Porta de Broadcast**: 54322 (UDP)
- **Porta de Conexão**: 54321 (TCP)
- **Tempo de Espera**: 10 segundos para descoberta
- **Intervalo de Broadcast**: 2 segundos

---

**Desenvolvido para o jogo Célula Secreta** 🧬


