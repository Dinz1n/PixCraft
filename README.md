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
✅ Configuração simples via `config.yml` e `products.yml`

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
# Exemplo de produto: ESPADA DE DIAMANTE
espada_de_diamante_01:
  displayname: "Espada de Diamante"
  price: 2.99
  include-tax: false
  reward:
    - "give {player} diamond_sword"

# Exemplo de produto: VIP
vip_01:
  displayname: "VIP"
  price: 10.00
  include-tax: false
  reward:
    - "lp user {player} parent set vip"
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
