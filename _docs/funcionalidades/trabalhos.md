# Trabalhos

Conceito da Fase 5, planejada para depois da Fase 4. Nada aqui está implementado, e o documentado pode mudar se aparecer um impedimento técnico (regra 49 do `AGENTS.md`). As decisões ficam em [`fase-5-trabalhos.md`](../perguntas/fase-5-trabalhos.md), nas perguntas 190 a 256 e 1136 a 1237. O conceito de cada trabalho fica na pasta [`trabalhos/`](trabalhos/) (pergunta 1148).

Números: [`tiers.md`](../tiers.md#trabalhos). Nomes e IDs previstos: [`itens.md`](../itens.md#previstos-na-fase-5).

## A ideia

Um mundo com vários Cubits trabalhando sozinhos, cada um com um trabalho. É sempre o mesmo robô: o card define o que ele sabe fazer, a área define onde, e as antenas mantêm esse lugar funcionando.

O mod funciona numa instância só com ele e suas dependências. Tudo aceita redstone, e cabos e outros mods são um extra (perguntas 238 e 240).

## Cards de Aprendizado de Máquina

- **Slot:** o card fica num slot exclusivo e sai quando o player quiser, sem perda. Sem card, o Cubit é generalista.
- **Quantidade:** um por Cubit, e o tier melhora o que o card faz.
- **Tier:** o Base não aceita cards, e cada card tem o seu tier mínimo, na tabela abaixo (pergunta 1145). Cada tier mantém os trabalhos do anterior e ganha novos.
- **Estado:** morrer e recolher preservam o card.
- **Seguindo:** age como generalista. O card só age em área marcada. A exceção é o Patrulheiro, que escolta o dono e quem o dono marcar com as armas (perguntas 1565 e 1655).
- **Item:** um por trabalho, com receita própria em JSON e materiais do jogo puro. Os cards empilham.
- **Receita:** como um pendrive, todo card junta a Card Casing, a Circuit Board e o item que simboliza o trabalho, numa cascata na Engineering Desk. As receitas estão em [`itens.md`](../itens.md#cards-de-trabalho) (perguntas 1540 a 1545). A receita de cada card libera com o Module do tier mínimo do trabalho, e a carcaça e a placa, com o Advanced Module (pergunta 1546).
- **Falas:** cada profissão fala uma de três frases ao receber o card e frases próprias durante o trabalho, para o dono por perto. As frases estão em [`falas.md`](../conceito/falas.md#profissões) (perguntas 1583 a 1590).
- **Custo:** gasta o consumo ao andar e mais um valor por ação, por card, sem gastar se não houver ação.

### Trabalhos por tier

| Tier | Trabalhos |
|---|---|
| Base | Nenhum. Não aceita cards |
| Avançado | Pescador, Fazendeiro, Pecuarista, Organizador e Cozinheiro (perguntas 2235 e 2236) |
| Elite | Os do Avançado, mais Patrulheiro Galático, Minerador, Construtor, Mecânico e Alquimista |
| Ultimate | Os mesmos do Elite. Nenhum trabalho espera o Ultimate, que só melhora os números de cada card (pergunta 1598) |

O tier também melhora o que o card faz, com mais velocidade, alcance ou opções. A evolução de cada card é proposta e é decidida no refinamento dele, com os números em [`tiers.md`](../tiers.md#trabalhos) (perguntas 1144 e 1154). A do Minerador e a do Patrulheiro Galático, no lugar do Defensor, já estão decididas (perguntas 1149, 1150 e 1563), e as do Pescador e do Fazendeiro foram decididas no card de cada um (perguntas 1185, 1221 e 1226).

### Os trabalhos

| Trabalho | Card | Tier mínimo | O que faz | Conceito |
|---|---|---|---|---|
| Pescador | `cubit:ml_fisher` | Avançado | Pesca na água da área, sem vara de verdade, pela tabela do jogo | [`pescador.md`](trabalhos/pescador.md) |
| Fazendeiro | `cubit:ml_farmer` | Avançado | Ara perto da água, planta com as sementes do baú, colhe o que amadureceu e replanta | [`fazendeiro.md`](trabalhos/fazendeiro.md) |
| Pecuarista | `cubit:ml_rancher` | Avançado | Alimenta, tosquia, recolhe ovos e controla o rebanho | [`pecuarista.md`](trabalhos/pecuarista.md) |
| Organizador | `cubit:ml_organizer` | Avançado | Recolhe e guarda nos baús certos, ou abastece destinos | [`organizador.md`](trabalhos/organizador.md) |
| Cozinheiro | `cubit:ml_cook` | Avançado | Leva comida crua aos fornos e guarda a pronta | [`cozinheiro.md`](trabalhos/cozinheiro.md) |
| Patrulheiro Galático | `cubit:ml_patroller` | Elite | Faz a ronda, procura hostis e ataca primeiro, com pistola laser e Plasma Blade | [`patrulheiro.md`](trabalhos/patrulheiro.md) |
| Minerador | `cubit:ml_miner` | Elite | Escava, abre túneis ou busca minérios, com a fornalha elétrica | [`minerador.md`](trabalhos/minerador.md) |
| Construtor | `cubit:ml_builder` | Elite | Aplaina e preenche, com os modos novos propostos | [`construtor.md`](trabalhos/construtor.md) |
| Mecânico | `cubit:ml_mechanic` | Elite | Recarrega, conserta, repara a vida e destrava os outros Cubits | [`mecanico.md`](trabalhos/mecanico.md) |
| Alquimista | `cubit:ml_alchemist` | Elite | Prepara poções em cadeia nos suportes de poções | [`alquimista.md`](trabalhos/alquimista.md) |

Os itens "Em aberto" de cada conceito são respondidos no refinamento do card daquele trabalho (pergunta 1153).

### Outras ideias

- **Talvez, ainda sem fase:** o Arquiteto, que copia uma estrutura de uma área para outra, e o Comerciante, que negocia com aldeões (pergunta 1146).
- **Descartados:** o Artesão, com o Crafter do jogo, e o Logístico, com os canais do storage wireless (pergunta 1146).

## Como o Cubit começa a trabalhar

1. Com o app Área do [Tablet](tablet.md#apps), o player escolhe o Cubit e desenha a área, ponto por ponto, em ângulo reto ([`modo-area.md`](modo-area.md#marcar-a-área)).
2. Ao aplicar o desenho, ele deixa de seguir e vai para a área, como no [modo área](modo-area.md) da Fase 4.2.
3. O Tablet também manda descarregar ou ir ao Dock, pelos botões `Descarregar` e `Ir para o Dock`.

Os baús são marcados com o app Descarga do Tablet, e a tela do Cubit tem uma aba Trabalho, com modo e filtros. Marcar os outros blocos do trabalho, como fornos e suportes de poções, usa o app Marcar do Tablet, que entra com o Organizador, o primeiro card que precisa dele (perguntas 1155, 1288 e 1297). O Pescador e o Fazendeiro não precisam: um acha sozinho a água da área, e o outro, a plantação e as sementes dos baús da Descarga dentro dela (perguntas 1186 e 1234). Se o player recolher o Cubit e colocá-lo de novo, precisa refazer a configuração.

## Regras de toda área

- **Limite:** o Cubit não sai da área marcada para trabalhar, e prefere o caminho por dentro dela (pergunta 2365).
- **Área carregada:** ela tem prioridade sobre o tier. Se o tier permitir uma área maior que a carregada, ele só atua na parte carregada.
- **Proteção:** o Cubit nunca mexe em blocos com dados, como baús, máquinas, Dock, antena e Servidor, nem na tag de blocos protegidos, que o pack pode editar. Cada card só mexe no seu tipo de bloco. As exceções são o Organizador, que só tira pela face de baixo das máquinas marcadas e põe nos baús e destinos marcados, e o Cozinheiro, que só põe e tira nos fornos marcados, os dois pela capability do NeoForge, sem quebrar o bloco (perguntas 1289 e 1316, [`organizador.md`](trabalhos/organizador.md#proteção) e [`cozinheiro.md`](trabalhos/cozinheiro.md#proteção)).
- **Vários Cubits:** há um número fixo por área, e cada um reserva o alvo antes de ir. Varreduras e ações têm limite por tick, e os robôs esperam a vez. Os Patrulheiros são a exceção: lutam em equipe, no mesmo alvo ([`patrulheiro.md`](trabalhos/patrulheiro.md#quem-ele-ataca), pergunta 1643).
- **Inventário cheio:** descarrega no baú marcado o que conseguir. Quando não conseguir mais, hiberna. O Patrulheiro nunca hiberna por inventário cheio (pergunta 1639).

## Base do trabalho

Entra com o card #193, a base do trabalho, sem trabalho próprio: o Pescador vem no card seguinte (perguntas 1140 e 1161). A base liga o card ao Cubit, mostra o trabalho e cria a hibernação fora do Dock.

### Card no Cubit

- **Slot:** fica na fileira de slots exclusivos, depois das tochas, como elas: apagado no Base, com `Unlocks at Advanced`. Aceita só um item da tag `cubit:ml_cards`, um por vez (pergunta 1162).
- **Tier:** cada tier aceita os trabalhos da lista `allowed_jobs` do config, com o padrão da [tabela acima](#trabalhos-por-tier). Um card que o tier do Cubit não aceita não entra no slot, e a dica diz o tier que ele pede. Um nome inválido na lista é ignorado e vai para o log (perguntas 1145 e 1163).
- **Item:** o card fica no item do Cubit, no componente `cubit:card_slot`, também no Damaged Cubit, e no NBT `CardSlot` da entidade, como as tochas (perguntas 191 e 1162).
- **Na base:** só existe o card do Pescador, `cubit:ml_fisher`, obtido por `/give`, sem receita e sem comportamento. A receita e o trabalho entram no card do Pescador (pergunta 1161).

### Cubit com card

- **Seguindo o player:** age como generalista. O card só age em área marcada (pergunta 193). A exceção é o Patrulheiro, que faz a escolta com as armas (perguntas 1565 e 1655).
- **Na área:** o Cubit com card está trabalhando. Até o trabalho do card existir, ele faz o mesmo que sem card: coleta, faz a ronda e descarrega (perguntas 340 e 1166). Cada card troca esse comportamento pelo do trabalho.
- **Estado:** `Working · Fisher` / `Trabalhando · Pescador`, no lugar de `In area`, no topo da tela e na lista do Tablet (pergunta 1168).

### Aba Trabalho

Uma aba `Work` / `Trabalho` na tela do Cubit e na tela do Cubit no Tablet, com um ícone feito por prompt (perguntas 1167 e 1169). Ela mostra o card, o nome do trabalho, o estado e, hibernando, o motivo, com o botão `Resume` / `Retomar`. Sem card, diz que o Cubit não tem trabalho. O modo e os filtros entram com cada trabalho. Com card ou sem card, ela também tem o intervalo de descarga, descrito em [`inventario.md`](inventario.md#descarregar-em-baú) (perguntas 1250 a 1254).

Com o card do Organizador, a linha da chave do trabalho dá lugar à linha `Mode` / `Modo`, com um botão que alterna entre `Organize` / `Organizar` e `Supply` / `Abastecer`, e a tela ganha a aba `Supply` / `Abastecer`, com os pedidos, descrita em [`organizador.md`](trabalhos/organizador.md#modo-abastecer) (perguntas 1290 e 1303).

### Hibernação fora do Dock

Estado semelhante ao desligado, mas com o inventário e as configurações acessíveis. O Cubit fica imóvel e sem tarefas, como dormindo (perguntas 198 e 1164).

- **Estado:** `HIBERNATING_AWAY`, no NBT `PowerState`, com o motivo no NBT `HibernationReason`. A hibernação no Dock continua como está e sai sozinha (perguntas 55 e 1164).
- **Motivo, na base:** só o inventário cheio, e o Pescador acrescenta o `Sem água na área` (pergunta 1188): o Cubit com card, na área, cheio e sem baú marcado com espaço. Sem card, ele segue como nas perguntas 1052 e 1088. Cada trabalho acrescenta os seus motivos (perguntas 196 e 1165). O Patrulheiro nunca hiberna por inventário cheio, para não deixar a área sem defesa (pergunta 1639).
- **Parado:** sem choque, coleta, ímã, tochas, falas e scanners, com a lanterna apagada e sem consumo. O painel solar continua carregando (pergunta 1170).
- **Na tela:** `Hibernating · Inventory full` / `Hibernando · Inventário cheio`, em azul, com a expressão de sono (perguntas 1168 e 1173).
- **Texto curto:** `I'm full and have nowhere to unload` / `Estou cheio e não tenho onde descarregar`, uma vez ao hibernar, e de novo se o `Retomar` não resolver (pergunta 1174).
- **Save:** um motivo desconhecido vira o inventário cheio, e o log avisa (pergunta 1175).
- **Voltar:** pelo `Retomar`, na aba Trabalho da tela do Cubit ou do Tablet. Se o motivo não sumiu, ele hiberna de novo e explica (pergunta 253). O `Follow` e o `Ir para o Dock` do Tablet também o tiram da hibernação, como um `Retomar` seguido da ordem. O `Descarregar` fica desabilitado, com a dica de usar o `Retomar` (pergunta 1170).
- **Dano:** não o acorda. Ele leva o dano sem reagir, como desligado, e pode morrer, virando o Damaged Cubit com o card e o estado guardados (pergunta 1171).
- **Sem card:** tirar o card do slot cancela a hibernação, que só existe com trabalho, e ele volta ao comportamento sem card na área (pergunta 1171).
- **Recolher:** Shift + clique continua recolhendo, e o clique direito continua abrindo a tela (pergunta 253).

## Network

O Network Server, o Solar Relay e a Signal Antenna, com o carregamento de chunks, o log e os Docks, estão em [`network.md`](network.md), autorizado na pergunta 2325 e planejado para a `0.2.0`. Com ela, todo o modo área passa a exigir a rede (perguntas 2202 e 2206). A antena com tiers e a viagem até o Dock com teleporte, previstas antes nas perguntas 225, 229, 243 e 246, não valem mais (perguntas 2199 e 2250).

## Storage wireless

- **Inventário virtual:** salvo no mundo, por dono e canal. A `Wireless Chest` na base e o upgrade do Cubit acessam o mesmo canal, de qualquer lugar e dimensão, sem carregar chunks.
- **Canal:** escolhido numa tela, com número e nome, como `Minérios` e `Fazenda`. A cor do corante pode colorir o bloco só como identificação. Um Cubit por baú, ou vários no mesmo.
- **Endgame:** só no Ultimate, como upgrade caro. Transferir custa FE por item, e cada canal tem tamanho fixo.
- **Segurança:** toda alteração passa pelo servidor, num ponto único, com teste de duplicação. Só o dono acessa.

## Ordem de entrega

A Fase 5 é entregue como um kanban, no GitHub Project "Cubit · Fase 5", um card por vez, e cada card é uma issue e um PR (perguntas 1137 e 1138). Um card só sai do Refinamento com o conceito em `_docs`, as perguntas respondidas, os números em `tiers.md`, o contrato listado e o roteiro manual escrito. Só fecha com os gates, as GameTests e o teste manual aprovados, e, num card de trabalho, com o teste com 20 a 30 robôs (pergunta 1144).

1. Consolidar o conceito da Fase 5 (#192)
2. Base do trabalho: slot do card, tier mínimo, estado de trabalho, aba Trabalho e hibernação fora do Dock com o `Retomar` (#193)
3. Pescador (#194)
4. Fazendeiro (#195)
5. Organizador (#196)
6. Cozinheiro (#197)
7. Patrulheiro Galático, no lugar do Defensor (#198)
8. XP Cubit e Dock, o XP como fluido no tanque do Cubit e do Dock (#244, [`xp.md`](xp.md))
9. Network, a rede sem fio, em 9 entregas ([`network.md`](network.md), #263 a #271, no lugar das #205 e #206, pergunta 2292)
10. Lenhador, uma profissão nova, ainda sem issue
11. Pecuarista (#201)
12. Mecânico (#203)
13. Trader, o Negociante, ainda sem issue ([`trader.md`](trabalhos/trader.md))
14. Explorador, uma profissão nova, ainda sem issue ([`explorador.md`](trabalhos/explorador.md))
15. Alquimista (#204)
16. Minerador (#199)
17. Construtor (#200)
18. Storage wireless (#207)

A ordem segue a pergunta 251, mudada na 1666, na 1777 e na 1788: depois do Patrulheiro, o XP Cubit e Dock, já entregue (perguntas 1787 a 1789), a Network, com o Servidor, o Solar Relay e a antena, em andamento (perguntas 1777 a 1779 e 2196 a 2366), e então o Lenhador, o Pecuarista, o Mecânico, o Trader, o Explorador e o Alquimista, e só então o Minerador e o Construtor (perguntas 1666, 1669 e 1927). Toda profissão entra com o seu visual, no próprio card, e a personalização visual por card (#202) saiu do roteiro (pergunta 1930).

## Pendente

- Os números marcados como "A definir" em [`tiers.md`](../tiers.md#trabalhos), cada um no card dele (pergunta 1144).
- Os detalhes do Organizador (pergunta 208), no card dele.
- O formato da tela do Cubit com a aba Trabalho e o botão `Retomar`, no card da Base.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-25 09:41 | Atualiza a Network na ordem de entrega, com as issues novas (perguntas 2292 e 2366). |
| Zennd | 2026-11-25 00:23 | Leva o Organizador e o Cozinheiro ao Advanced e cita a área desenhada (perguntas 2235, 2236, 2240 e 2365). |
| Zennd | 2026-11-24 22:42 | Troca o Servidor, a antena e a viagem até o Dock pelo `network.md` (pergunta 2325). |
| Zennd | 2026-11-23 03:25 | Põe o Trader depois do Mecânico e tira a personalização visual por card do roteiro (perguntas 1927 e 1930). |
| Zennd | 2026-11-22 23:12 | Liga o XP Cubit e Dock à #244 e ao conceito (pergunta 1851). |
| Zennd | 2026-11-22 21:03 | Põe o XP Cubit e Dock depois do Patrulheiro, antes do Servidor e das Antenas (perguntas 1787 a 1789). |
| Zennd | 2026-11-22 17:59 | Põe o Servidor e as Antenas, com a energia sem fio e o carregamento de chunks, logo depois do Patrulheiro (perguntas 1777 a 1779). |
| Zennd | 2026-11-22 16:59 | Troca o Defensor pelo Patrulheiro e acrescenta a exceção do inventário cheio, a equipe e a personalização na ordem (perguntas 1639, 1643 e 1765) |
| Zennd | 2026-11-22 16:30 | Atualiza a numeração das regras do `AGENTS.md` (pergunta 1716). |
| Zennd | 2026-11-22 13:30 | Acrescenta o Explorador à ordem de entrega (perguntas 1668 a 1670). |
| Zennd | 2026-11-22 13:10 | Troca a ordem de entrega dos trabalhos, com o Lenhador (perguntas 1666 e 1667). |
| Zennd | 2026-11-22 13:00 | Registra as exceções do Patrulheiro na escolta e na hibernação (perguntas 1565, 1639 e 1655). |
| Zennd | 2026-11-22 08:03 | Leva o Construtor, o Mecânico e o Alquimista para o Elite (pergunta 1598). |
| Zennd | 2026-11-21 23:14 | Acrescenta as falas das profissões (perguntas 1583 a 1590). |
| Zennd | 2026-11-21 23:02 | Troca o Defensor pelo Patrulheiro Galático (perguntas 1550 a 1582). |
| Zennd | 2026-11-21 21:28 | Libera as receitas dos cards pelo Module do tier (pergunta 1546). |
| Zennd | 2026-11-21 21:21 | Registra a receita comum dos cards (perguntas 1540 a 1545). |
| Marcelo Fabiano | 2026-11-19 23:04 | Abre a exceção do Cozinheiro na proteção (pergunta 1316). |
| Marcelo Fabiano | 2026-11-19 22:13 | Acrescenta a linha `Mode` e a aba `Supply` do Organizador (perguntas 1290 e 1303). |
| Marcelo Fabiano | 2026-11-19 21:34 | Leva a marcação dos blocos ao app Marcar e abre a exceção do Organizador na proteção (perguntas 1288, 1289 e 1297). |
| Marcelo Fabiano | 2026-11-15 14:05 | Acrescenta o intervalo de descarga na aba Trabalho (perguntas 1250 a 1254). |
| Marcelo Fabiano | 2026-11-15 10:40 | Liga o conceito do Fazendeiro, que ara e planta com as sementes do baú (perguntas 1215 a 1237). |
| Marcelo Fabiano | 2026-11-14 23:10 | Liga o conceito do Pescador, que usa o app Área e acrescenta o motivo sem água (perguntas 1181 a 1204). |
| Marcelo Fabiano | 2026-11-14 20:52 | Define a base do trabalho: o slot do card, o tier, a aba Trabalho e a hibernação fora do Dock (perguntas 1160 a 1172). |
| Marcelo Fabiano | 2026-11-14 20:27 | Consolida a Fase 5: os trabalhos por tier, o Mecânico e o Alquimista, os conceitos na pasta `trabalhos/`, o Tablet no lugar do controle e a ordem do board (perguntas 1136 a 1156). |
| Marcelo Fabiano | 2026-11-13 16:40 | Troca o Cubit Remote pelo app Área do Tablet. |
| Marcelo Fabiano | 2026-11-13 09:05 | Recebe a fornalha de minérios no Minerador. |
| Marcelo Fabiano | 2026-11-06 13:34 | Adiciona a lanterna de cabeça do Minerador. |
| Marcelo Fabiano | 2026-11-06 11:10 | Troca 'especialidade' por 'trabalho' e 'card de especialidade' por 'card de Aprendizado de Máquina'. |
| Marcelo Fabiano | 2026-11-05 21:36 | Marca os modos do Minerador como ideia inicial. |
| Marcelo Fabiano | 2026-11-05 12:08 | Consolida o conceito dos robôs especializados. |
