# 💰 PixCraft  

[![Minecraft](https://img.shields.io/badge/Minecraft-Plugin-darkgreen.svg)]() [![MercadoPago](https://img.shields.io/badge/Payments-MercadoPago-lightblue.svg)]() [![License: AGPL-3.0](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)](LICENSE)

PixCraft é um plugin para **Minecraft** que implementa um sistema de pagamentos via **QR Code** integrado ao **MercadoPago**, permitindo que jogadores realizem compras dentro do jogo de forma simples e segura.  

---

## 🚀 Funcionalidades principais  

- ✅ Integração completa com o **MercadoPago**  
- ✅ Pagamento via **QR Code** diretamente no jogo  
- ✅ **GUI** para confirmação de pedidos  
- ✅ Integração com **webhook no Discord** para notificação de novos pagamentos  
- ✅ Loja com interface de **baú**  
- ✅ Nome do comando para abrir a loja **personalizável**  

---

## 📦 Comandos e Permissões  

| Comando     | Descrição                   | Permissão        |
|-------------|-----------------------------|------------------|
| `/pixcraft` | Comando principal do plugin | `pixcraft.admin` |
| `/loja`     | Abre a loja do plugin       | `pixcraft.shop`  |

### Subcomandos  

| Comando     | Subcomando | Descrição          |Permissão               |
|-------------|------------|--------------------|------------------------|
| `/pixcraft` | `reload`   | Recarrega o plugin |`pixcraft.command.admin`|
| `/pixcraft` | `menu`     | Abre um menu       |`pixcraft.command.menu` |
| `/pixcraft` | `product`  | Compra um produto  |`pixcraft.command.product`|

---

## ⚙️ Instalação  

1. Baixe a última versão do plugin em [realeses](https://github.com/Dinz1n/PixCraft/releases).  
2. Coloque o arquivo `.jar` na pasta `plugins` do seu servidor.  
3. Reinicie o servidor.  
4. Configure as credenciais do **MercadoPago** no arquivo `config.yml`.  

---

## 📷 Demonstração  
### Pagamento de um produto de teste.
![Demonstração do PixCraft](docs/demo.gif)
### A atualização no mercadopago é quase imediata.
![Mensagem do mercadopago](docs/demo-mercadopago.png)
### Mas a notificação no discord pode levar alguns segundos.
![Notificação do Discord](docs/demo-notificacao-discord.png)

---

## 🤝 Contribuindo  

Contribuições são sempre bem-vindas!  

1. Faça um fork do repositório  
2. Crie uma branch para sua feature ou correção (`git checkout -b minha-feature`)  
3. Commit suas alterações (`git commit -m 'Minha nova feature'`)  
4. Envie sua branch (`git push origin minha-feature`)  
5. Abra um Pull Request 🚀  

---

## 📜 Licença  

Este projeto está sob a licença **AGPL 3.0** – veja o arquivo [LICENSE](LICENSE) para mais detalhes.  
