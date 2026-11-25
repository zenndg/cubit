# Glossário

O inglês é o idioma base do mod, e o jogo também tem a tradução para o português, no `pt_br.json`, com os nomes traduzidos, como `Base de Indução` (perguntas 712 e 733). Os documentos podem usar o termo em português da primeira coluna.

## Nomes definidos

| Termo nos documentos | Nome no jogo |
|---|---|
| Mod e robô | Cubit |
| Tablet, que substitui o controle remoto | Cubit Tablet |
| App do Tablet | Tablet app |
| Apps do Tablet: Abrir, Alvo, Dock, Área, Descarga, Marcar, Portas e Escolta | Open, Target, Dock, Area, Unload, Mark, Doors e Escort |
| Cubit danificado | Damaged Cubit |
| Caixa de ferramentas | Toolbox |
| Mesa de Engenharia | Engineering Desk |
| Fabricação em cascata, da Mesa de Engenharia | Cascade |
| Base, Dock ou estação de carga | Induction Dock |
| Tiers: Base, Avançado, Elite e Ultimate | Base, Advanced, Elite e Ultimate |
| Upgrades | Advanced, Elite e Ultimate Module |
| Power Bank | Base, Advanced, Elite e Ultimate Power Bank |
| Servidor da Rede ou Servidor, planejado | Network Server |
| Relé Solar, planejado | Solar Relay |
| Antena de Sinal ou antena, planejada | Signal Antenna |
| Rede, planejada ([`network.md`](funcionalidades/network.md)) | Network |
| Reserva do chunk, planejada (pergunta 2218) | UPS, e Nobreak em português |
| Voltar ao trabalho, planejado | Back to work |
| Baú wireless | Wireless Chest |
| Upgrade do storage wireless | Wireless Storage Upgrade |
| Trabalho | Job |
| Aba Trabalho | Job tab |
| Card de Aprendizado de Máquina | Machine Learning card |
| Aprendizado de Máquina: Minerador, Fazendeiro, Pescador, Pecuarista, Patrulheiro Galático, Organizador, Cozinheiro e Construtor | Machine Learning: Miner, Farmer, Fisher, Rancher, Galactic Patroller, Organizer, Cook e Builder |
| Generalista | Generalist |
| Lanterna | Lamp |
| Scanners | Scanners |
| Falas | Chatter |
| Pacífico | Peaceful |
| Contra-atacar | Retaliate |
| Agressivo | Aggressive |
| Só o alvo | Focus |
| Pistola laser e globo de laser, do Patrulheiro | Laser pistol e laser orb |
| Lâmina de plasma, do Patrulheiro | Plasma Blade |
| Escolta marcada, do Patrulheiro | Escort |
| Fluido de XP, o XP guardado nos tanques do Cubit e do Dock ([`xp.md`](funcionalidades/xp.md)) | Cubit XP Fluid |
| Tanque de XP | XP tank |

Os IDs estão em [`itens.md`](itens.md).

## Termos técnicos

- **FE (Forge Energy):** unidade de energia padrão entre os mods de NeoForge.
- **FE/t:** FE por tick. O jogo roda a 20 ticks por segundo.
- **mB (milibalde):** unidade de fluido. Um balde tem 1.000 mB, e 1 ponto de XP vale 20 mB do Cubit XP Fluid.
- **Tier:** nível de evolução do robô.
- **Base:** o Induction Dock, bloco fixo de recarga.
- **Robô que segue:** o único Cubit do player que o acompanha naquele momento.
- **Vínculo:** o registro, nos dados do player, de qual Cubit o segue.
- **Condição negativa:** fogo, lava, bloco que fere ou efeito nocivo, que custam energia extra.
- **Lanterna:** a luz do Cubit no escuro, que gasta energia enquanto acesa e acende as pontas das antenas.
- **Claridade da tela:** a luz fraca que a tela acesa emite, sem gastar energia.
- **Chave:** o que o player liga ou desliga para um recurso do Cubit.
- **Varredura:** uma leitura dos scanners em volta do Cubit, com um custo fixo de energia.
- **Minério raro:** minério de uma lista de tags no config, que avisa com som e surpresa mesmo fora do filtro.
- **Filtro do scanner:** os minérios que o dono pediu ao Cubit, clicando nele com um minério na mão.
- **Coleta:** o Cubit pega o item do chão que encosta nele, desde o Base.
- **Ímã:** do Advanced em diante, puxa até o Cubit os itens no raio do tier, por um custo por item puxado.
- **Busca (`Fetch`):** desde o Base, o Cubit que segue anda até os itens no chão no raio do tier em volta do dono, pega e volta a seguir.
- **Filtro de coleta:** a lista branca ou negra dos itens que a coleta, o ímã e a busca pegam, com os slots do tier.
- **Power Bank:** bateria portátil de um tier, que recebe energia só no slot do Induction Dock e entrega só ao Cubit, pelo slot de energia ou pela receita de religar na grade.
- **Recharge Splash:** mistura de arremesso que recupera, aos poucos, parte da vida e da energia dos Cubits do dono que a arremessou.
- **Pedido:** a receita e a quantidade que a Engineering Desk fabrica sozinha, uma unidade por vez.
- **Cascata:** em todos os tiers, a Engineering Desk passa sozinha as peças prontas da saída para a entrada, para continuar o pedido.
- **Fabricação em cascata:** no último tier, a Engineering Desk faz sozinha todas as peças até o item escolhido.
- **Plano:** as tarefas em série de uma fabricação em cascata, com os materiais reservados num estoque próprio.
- **Marcação:** a marca de um item na grade da Engineering Desk, que mostra a receita sem ser um item real.
- **Slot fantasma:** slot do filtro que guarda uma cópia do item, sem consumi-lo.
- **Bloco que fere:** bloco que o jogo classifica como perigoso na rota, como magma e cacto.
- **Ativo:** Cubit ligado, fora da base.
- **Inativo:** Cubit desligado.
- **Hibernação:** Cubit imóvel, sem tarefas, com o inventário e as configurações acessíveis. Na base, ele carrega e sai sozinho. Fora dela, na Fase 5, só volta pelo botão `Retomar` ou pelo controle.
- **Trabalho:** o que um card de Aprendizado de Máquina ensina ao Cubit, na Fase 5. Sem card, ele é generalista.
- **Cubit com trabalho:** o Cubit com um card no slot. O robô é sempre o mesmo.
- **Área de trabalho:** volume marcado com o controle, onde o card age.
- **Slot exclusivo:** slot reservado para tochas ou energia (Power Bank ou redstone) e, na Fase 5, para o card de Aprendizado de Máquina.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-24 22:42 | Atualiza os nomes da Network (perguntas 2218 e 2275). |
| Zennd | 2026-11-23 16:20 | Acrescenta a cascata, a fabricação em cascata e o plano (pergunta 2043). |
| Zennd | 2026-11-22 23:12 | Acrescenta o Cubit XP Fluid, o tanque de XP e o mB (pergunta 1850). |
| Zennd | 2026-11-22 13:00 | Acrescenta os apps do Tablet, as armas e a escolta marcada do Patrulheiro (perguntas 1554, 1655 e 1665). |
| Zennd | 2026-11-21 23:02 | Troca o Defensor pelo Patrulheiro Galático (perguntas 1550 a 1582). |
| Zennd | 2026-11-21 12:45 | Adiciona a Mesa de Engenharia, o pedido e a marcação (perguntas 1424 a 1484). |
| Marcelo Fabiano | 2026-11-21 11:30 | Adiciona a Recharge Splash. |
| Marcelo Fabiano | 2026-11-13 16:40 | Troca o controle remoto pelo Tablet. |
| Marcelo Fabiano | 2026-11-13 09:50 | Adiciona a busca dos itens do chão. |
| Marcelo Fabiano | 2026-11-13 09:05 | Tira a fornalha e o carregador do slot exclusivo. |
| Marcelo Fabiano | 2026-11-12 22:50 | Adiciona a Power Bank. |
| Marcelo Fabiano | 2026-11-12 21:45 | Adiciona a coleta, o ímã, o filtro de coleta e o slot fantasma. |
| Marcelo Fabiano | 2026-11-12 15:10 | Adiciona o modo Focus. |
| Marcelo Fabiano | 2026-11-08 00:11 | Cita a tradução para o português. |
| Marcelo Fabiano | 2026-11-07 16:54 | Adiciona as falas. |
| Marcelo Fabiano | 2026-11-07 16:10 | Adiciona os scanners, a varredura, o minério raro e o filtro. |
| Marcelo Fabiano | 2026-11-06 13:34 | Adiciona a lanterna, a claridade da tela e a chave. |
| Marcelo Fabiano | 2026-11-06 11:10 | Adiciona trabalho, aba Trabalho e o robô único. |
| Marcelo Fabiano | 2026-11-06 11:10 | Troca 'especialidade' por 'trabalho' e 'card de especialidade' por 'card de Aprendizado de Máquina'. |
| Marcelo Fabiano | 2026-11-05 21:02 | Adiciona vínculo, condição negativa, bloco que fere e o upgrade do storage, e cita o slot do card. |
| Marcelo Fabiano | 2026-11-05 12:08 | Adiciona os termos dos robôs especializados e amplia a hibernação. |
| Marcelo Fabiano | 2026-11-05 00:12 | Adiciona o Cubit danificado, a caixa de ferramentas, o Dock e o Avançado. |
| Marcelo Fabiano | 2026-11-04 17:26 | Adiciona o robô que segue e a família de robôs. |
| Marcelo Fabiano | 2026-11-04 17:15 | Renomeia para português e atualiza os caminhos. |
| Marcelo Fabiano | 2026-11-04 16:57 | Define os nomes aprovados e os estados do Cubit. |
| Marcelo Fabiano | 2026-11-04 15:23 | Registra as decisões e o glossário. |
