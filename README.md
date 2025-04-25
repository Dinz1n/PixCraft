# PixCraft 💰
🚀 **Automatize pagamentos via Pix no Minecraft usando o Mercado Pago!**

## 📌 Descrição
PixCraft é um plugin para **Minecraft** que integra pagamentos via **Pix** usando o **Mercado Pago**.  
Ele permite que servidores automatizem transações, garantindo que os jogadores recebam seus produtos automaticamente após o pagamento.

## ⚙️ Funcionalidades
✅ Integração com **Mercado Pago**  
✅ Verificação periódica de pagamentos  
✅ Suporte a notificações via **Webhook** (incluindo Discord)  
✅ Geração automática de **QR Code** para pagamento  
✅ Configuração simples via `config.yml`, `products.yml` e `payment_confirmation_gui.yml`

## 📦 Instalação
1. **Baixe o plugin** em [Releases](https://github.com/Dinz1n/PixCraft/releases).
2. **Coloque o arquivo .jar na pasta `plugins/` do seu servidor**.
3. **Reinicie o servidor** para gerar os arquivos de configuração.

## 🛠 Configuração
### `config.yml` (Configuração Principal)
Aqui você define as credenciais do **Mercado Pago**, a forma de verificação de pagamentos e a integração com **Discord Webhook**.

```yaml
mercadopago:
  access-token: "TOKEN"

  # Configuração do webhook para notificações de pagamento.
  webhook:
    enabled: false  # Se "false", o plugin usará a verificação periódica.
    port: 8080      # Porta onde o servidor HTTP interno será iniciado.

discord:
  notifications:
    enabled: false
    webhook-url: "webhook_url"
    embed:
      player-head-icon: true  # Exibir o avatar do jogador no embed.
      color: "#1aff00"        # Cor da borda do embed no Discord.
      title: "Nova venda efetuada!"
      description: "Comprador: {player}"
      field:
        enabled: true
        title: "Produto: {product} | Valor pago: R${price}"
        description: "Data da compra: {date}"
```
### `products.yml` (Configuração de Produtos)
Este arquivo define os produtos disponíveis para compra e seus respectivos comandos de recompensa.

```yaml
# 📌 Como configurar seus produtos:
#
# Cada produto deve ter um identificador único (como "vip_01" ou "espada_de_diamante_01").
# Dentro dele, você pode definir nome, preço, cobrança de taxa e os comandos de recompensa.

# Exemplo de produto: ESPADA DE DIAMANTE
espada_de_diamante_01:
  # 🏷️ Nome do Produto:
  # Esse nome aparecerá no Discord e na sua conta do Mercado Pago quando alguém comprar este item.
  name: "Espada de Diamante"

  # 💰 Preço do Produto:
  # Defina o valor em reais (R$). Use PONTO ao invés de VÍRGULA para valores decimais.
  # Exemplo: 2.99 (correto) | 2,99 (errado)
  price: 2.99

  # 📌 Incluir Taxa?
  # O Mercado Pago cobra uma taxa de 0,99% por pagamento via PIX. Se ativado (true),
  # o valor do produto será ajustado para que você receba exatamente o preço definido acima.
  # Caso contrário (false), a taxa será descontada do valor final.
  include-tax: false

  # 🎁 Recompensa para o jogador:
  # Aqui você define os comandos que serão executados automaticamente após o pagamento.
  # Use "{player}" para referenciar o nome do jogador que comprou.
  reward:
    - "give {player} diamond_sword"  # O jogador recebe uma espada de diamante.

  # 💎 Ícone do produto:
  # Esse ícone irá aparecer no GUI de confirmação do pedido.
  icon:
    # Define o Material do ítem.
    # Se não souber o nome do material, você pode acessar o site:
    # https://minecraft-ids.grahamedgecombe.com
    # Alguns materiais não funcionarão, mas são apenas alguns bem específicos.
    material: "DIAMOND_SWORD"

    # Define o nome do ítem.
    displayname: "&b&lEspada de Diamante"

    # Define a lore do item.
    lore:
      - ""
      - "&a&lR$2,99 no &b&lPIX"

    # Define a quantidade do ítem.
    amount: 1

    # Define se o ítem terá o efeito visual de encatamento.
    enchanted: false

# Exemplo de produto: VIP
vip_01:
  name: "VIP"
  price: 10.00
  include-tax: false
  reward:
    - "lp user {player} parent set vip"  # O jogador recebe o grupo VIP no LuckPerms.
  icon:
    material: "EMERALD"
    displayname: "&a&lVIP"
    lore:
      - ""
      - "&a&lR$10,00 no &b&lPIX"
    amount: 1
    enchanted: true
```
### `payment_confirmation_gui.yml` (configuração do GUI de confirmação do pedido)
Nesse arquivo você pode customizar várias características do GUI de confirmação.

```yaml
buttons:
  confirm-button:
    material: "LIME_WOOL"
    displayname: "&aConfirmar"
    lore:
    amount: 1
    enchanted: false

  cancel-button:
    material: "RED_WOOL"
    displayname: "&cCancelar"
    lore:
    amount: 1
    enchanted: false

gui:
  title: "Confirmar Pedido"
  rows: 3
  slots:
    confirm-button: 11
    product: 13
    cancel-button: 15
```

## 🔧 Comandos & Permissões
| Comando         | Permissão      | Descrição                                |  
|----------------|----------------|------------------------------------------|  
| `/pc reload`   | `pixcraft.use` | Recarrega a configuração                |  
| `/pc buy {produto}` | `pixcraft.use` | Gera um QR Code de pagamento para o produto |  

## 📝 To-Do (Futuras Atualizações)
- [ ] Loja em interface baú
- [ ] Logs detalhados de transações
- [ ] Suporte a outras formas de pagamento

## 📜 Licença
Este projeto é open-source sob a licença [MIT](LICENSE).  
