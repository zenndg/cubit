# Peças de montagem

O Cubit e as Power Banks são montados a partir de peças, numa cascata de receitas (issue #221, perguntas 1338 a 1351). As grades estão em [`itens.md`](../itens.md#peças-de-montagem).

As [peças compartilhadas](#peças-compartilhadas), de vários itens, são um conceito novo, refinado nas perguntas 2118 e 2131 a 2154 e autorizado na 2155, na issue #259. O ajuste do Neural Core, dos cards e da Power Bank (perguntas 2162 a 2169) foi autorizado na 2170.

## Por quê

- **O craft conta como o Cubit é feito:** carcaça, visor, esteiras e o núcleo que pensa, como na prancha de conceito.
- **O catálogo da Engineering Desk e o JEI mostram a árvore:** o player vê as peças antes do Cubit.
- **Ponto de integração para os packs:** uma peça pode ser sobrescrita sozinha com KubeJS, sem mexer no resto.

## Cubit

- **Três peças só dele:** Cubit Casing, Cubit Visor e Cubit Treads. A quarta, o Neural Core, virou [peça compartilhada](#peças-compartilhadas) (perguntas 1340, 1349 e 2163).
- **Só Overworld:** a receita final e as peças usam só materiais do Overworld, porque o Cubit é o companheiro do começo do jogo. A lâmpada de redstone, que leva glowstone do Nether, sai do Cubit (pergunta 1343).
- **Montagem:** as três peças só dele, o Neural Core, o Tanque de XP, o HD e o Painel Solar, sem forma, em qualquer posição da grade (perguntas 1346 e 2146). O Cubit sai como hoje: tier Base, com 25% de bateria e a vida cheia.
- **Núcleo:** é o cérebro que todo Cubit tem, sem cor de tier. Não dá trabalho nenhum: o trabalho vem do card de Aprendizado de Máquina (pergunta 1340).

## Power Bank

- **Carcaça e células:** uma Power Bank Casing, igual para todos os tiers, e uma Power Cell por tier, que leva a cor dele (pergunta 1347). As células são encadeadas: cada uma leva a anterior no centro, e a Base é a mais barata, só com materiais do Overworld (pergunta 1354).
- **Do zero:** a Power Bank Casing e a célula do tier, sem forma, em qualquer tier. A Power Bank sai vazia, como hoje (pergunta 1355).
- **Subir de tier:** a mesma cruz da célula do tier, com a Power Bank anterior no centro, com o tipo `cubit:power_bank_upgrade`. A célula de dentro sobe junto, e a carga passa inteira para a nova. Se não couber, a receita não aparece, como hoje (perguntas 907 e 1355).
- **Mesmo custo:** montar do zero e subir de tier custam o mesmo, e nada é pago duas vezes (pergunta 1355).
- **As células não guardam energia:** são só peças, sem carga e sem barra.

## Peças compartilhadas

Seis peças novas, cada uma com uma função que vários itens do mod têm em comum: a receita mostra o que o item faz. O conceito é do desenvolvedor (perguntas 2118 e 2133 a 2136).

### Regras

- **Só material:** uma peça só serve de material em outro craft. Nunca é colocada no mundo nem usada como ferramenta (pergunta 2133).
- **Mesa:** todas são feitas na [Engineering Desk](mesa-de-engenharia.md), que continua na mesa de craft comum, só com itens do jogo puro. Os 2 baús dela fazem o papel dos 2 HDs (pergunta 2133).
- **HD:** cada inventário de um item pede 1 HD, e um item com entrada e saída, 2. Os 2 do Trader vão à receita do Card Trader, quando ele entrar (perguntas 2133 e 2148).
- **Célula:** a receita de um bloco com tier leva sempre a Célula Base, e ele sobe com os Modules (pergunta 2147).
- **Uma função comum:** uma peça só vale se representa uma função de vários itens. O Sensor e a Chapa Reforçada ficaram de fora (pergunta 2133).

### As peças

| Peça | ID | Linha 1 | Linha 2 | Linha 3 | Liberada por | Onde entra |
|---|---|---|---|---|---|---|
| XP Tank / Tanque de XP, sempre vazio | `cubit:xp_tank` | Vidro, ferro, vidro | Vidro, balde, vidro | Vazio, ferro, vazio | Desde o início | Cubit e Dock |
| Hard Drive / HD | `cubit:hard_drive` | Ferro, baú, ferro | Redstone, ouro, cobre | | Desde o início | Cubit e Dock |
| Solar Panel / Painel Solar | `cubit:solar_panel` | 3 painéis de vidro | 3 lápis-lazúlis | Cobre, redstone, cobre | Desde o início | Cubit e Dock |
| Display Panel / Tela | `cubit:display_panel` | 3 painéis de vidro | Ferro, diamante, ferro | Redstone, obsidiana, cobre | Desde o início | Visor do Cubit, Tablet e Dock |
| Servo Motor / Servomotor | `cubit:servo_motor` | Vazio, cobre, vazio | Ferro, pistão, ferro | Vazio, redstone, vazio | Desde o início | Esteiras do Cubit e Dock |
| Induction Coil / Bobina de Indução | `cubit:induction_coil` | Pistão, cobre, pistão | Ferro, redstone, ferro | | Cubit, junto do Dock | Dock |

As cinco primeiras usam só materiais do Overworld, porque fazem parte do primeiro Cubit (perguntas 1343, 2136, 2149 e 2150).

### As receitas que mudam

| Item | Receita nova | Pergunta |
|---|---|---|
| Visor do Cubit | 1 Tela e 1 diamante, sem forma | 2142 |
| Esteiras do Cubit | 3 ferros em cima e 2 Servomotores embaixo, nas pontas | 2143 e 2150 |
| Cubit | As 4 peças de hoje, 1 Tanque de XP, 1 HD e 1 Painel Solar, sem forma | 2135 e 2146 |
| Induction Dock | 1 HD, 1 Tanque de XP, 1 Célula Base, 1 Bobina de Indução, 1 Tela, 1 Servomotor e 1 Painel Solar, sem forma | 2138, 2144 e 2147 |
| Cubit Tablet | Quartzo, redstone, quartzo / quartzo, Tela, quartzo | 2145 |

As outras não mudam: Carcaça do Cubit, grade do Neural Core, Power Banks e células, Modules, Card Casing, Toolbox, Mistura de Recarga, conserto e recarga (pergunta 2135).

### Neural Core, cards e Power Bank

O ajuste de 2162 a 2169, autorizado na 2170:

| Item | Como fica | Pergunta |
|---|---|---|
| Neural Core / Núcleo Neural | O Cubit Neural Core muda de nome e de ID, `cubit:neural_core`, com a mesma grade, e vira peça compartilhada: Cubit, Advanced Module e todo card | 2163, 2166 e 2167 |
| Circuit Board / Placa de Circuito | Passa a `cubit:circuit_board` e a peça compartilhada: cards e Power Bank Casing. Esmeralda, comparador, esmeralda / três ouros, liberada com o Cubit | 2165 a 2168 |
| Cards de profissão | Card Casing, Circuit Board, Neural Core e o item do trabalho, sem forma. A liberação, pelo Module do tier, não muda | 2164 |
| Power Bank Casing | Quartzo, Circuit Board, quartzo / quartzo, vazio, quartzo / quartzo, Induction Coil, quartzo. As Power Banks e o upgrade não mudam, e montar do zero e subir de tier continuam com o mesmo custo (pergunta 1355) | 2165 e 2169 |

- **IDs:** mudam sem alias, porque só os mundos de teste do desenvolvedor têm os itens antigos, que somem (pergunta 2166).
- **Catálogo:** o Neural Core e a Circuit Board passam para a categoria Shared parts. A Card Casing continua em Cards (pergunta 2167).
- **Guia:** a numeração passa a ter o Neural Core entre as peças do início e a Circuit Board com o Cubit, ainda de 1 a 35, com a imagem refeita.

- **Custo do primeiro Cubit:** 3 diamantes, 3 obsidianas, 2 pistões e 2 tochas de redstone, como antes, com 20 ferros (3 no balde), 9 redstones, 6 cobres, 6 painéis de vidro, 4 vidros, 3 lápis-lazúlis, 1 ouro e 1 baú, em 11 crafts (pergunta 2146).
- **Catálogo:** a categoria nova, `parts` no `desk_category`, aparece como Shared parts / Peças compartilhadas, com o Servomotor como ícone, numa barra com os ícones de 12 px de hoje (perguntas 2141, 2159 e 2160). As peças de hoje continuam nas categorias delas (perguntas 2141 e 2149).

### Network, provisório

Entram com o Servidor e as Antenas, e serão revistos lá (perguntas 2137 e 2139): o Transceptor (1 Placa de Circuito, 1 pérola do ender, 2 cobres e 1 redstone), a Âncora de Chunk (3 pérolas do ender, 3 obsidianas, 2 ouros e 1 diamante), as receitas do Servidor, do Solar Relay e da Signal Antenna e as peças próprias deles, a Carcaça e o Mastro. O Tablet fica sem Transceptor (pergunta 2148).

### Detalhes completados pelo agente

Não foram perguntados e foram autorizados junto do conceito (pergunta 2155):

1. As seis peças empilham até 64, ficam na aba `cubit:main`, depois das peças de hoje, e o clique direito com elas não faz nada.
2. Nenhum save muda: as peças não têm data components, e os IDs das receitas mudadas continuam os mesmos, para os packs. Os itens já feitos continuam valendo, menos o Neural Core e a Circuit Board, que mudaram de ID depois (pergunta 2166).
3. Os materiais são os itens exatos, como nas receitas de hoje: vidro e painel de vidro sem cor, e lingotes de ferro, ouro e cobre.
4. O balde vazio é gasto, sem devolução.
5. A categoria Parts fica logo depois da Robot na barra do catálogo.
6. O Visor, as Esteiras e o Cubit continuam liberados desde o início, e o Dock e o Tablet, com o Cubit.
7. Um plano da fabricação em cascata em andamento, com uma receita que mudou, devolve tudo, como já faz (pergunta 2010).
8. As texturas são de 16×16, no estilo das peças de hoje, por prompts em `tmp/arte/pecas/prompt.md`, aprovadas antes do código.
9. O guia do player e a imagem da árvore ganham a numeração nova, e o teste da árvore alcançável passa a valer para as peças.

### Contrato

| Onde | Nome | Conteúdo |
|---|---|---|
| Item e receita | Os seis IDs da tabela acima, `cubit:neural_core` e `cubit:circuit_board` | As peças, com o mesmo ID na receita. Saem `cubit:general_robot_core` e `cubit:ml_circuit_board`, sem alias (pergunta 2166) |
| Receita | `cubit:general_robot_visor`, `cubit:general_robot_treads`, `cubit:general_robot`, `cubit:induction_dock`, `cubit:tablet`, `cubit:power_bank_casing` e os cards `cubit:ml_*` | O mesmo ID, com a receita nova. O Visor e o Dock passam a ser sem forma |
| Campo da receita | `desk_category`: `parts` | A categoria nova |
| Textura | `textures/item/{id}.png` | As seis peças, e o Neural Core e a Circuit Board com os mesmos pixels de antes |
| Tradução | `item.cubit.{id}` e a categoria | Em inglês e português, com as mesmas chaves |

### Visual

As seis texturas são imagens geradas por prompt, reduzidas a 16×16 e aceitas nas perguntas 2156 e 2158: o tanque de vidro vazio, o disco rígido aberto, a placa solar, a tela desligada com o cabo de cobre, o motor com o enrolamento e a engrenagem, e a bobina chata de cobre. Nenhuma pede som.

### Entrega

Uma issue e um PR: as texturas aprovadas, os itens, as receitas, a categoria, o guia do player, a árvore e a imagem dela, refeita por prompt (perguntas 2151 e 2152).

## Regras das peças

- **Pilha:** todas empilham até 64, como os Modules.
- **Criativo:** ficam na aba `cubit:main`.
- **Uso:** nenhuma peça tem uso fora da [Engineering Desk](mesa-de-engenharia.md). O clique direito com elas não faz nada.
- **Catálogo:** as receitas saem da Engineering Desk. O Cubit, as três peças só dele e as compartilhadas do começo ficam liberados desde o início, e o resto segue a ordem de [`itens.md`](../itens.md#progressão-das-receitas) (perguntas 1496 e 1498).
- **Modules:** seguem a mesma linha das células, numa cruz com o Module anterior no centro. O Advanced nasce do Neural Core (perguntas 1357, 1358 e 2163). As grades estão em [`itens.md`](../itens.md#modules).
- **O que não muda:** a Toolbox, o conserto do Damaged Cubit e a recarga pela grade. O Dock e o Tablet passam às [peças compartilhadas](#peças-compartilhadas).

## Visual

Texturas de item 16×16, no estilo das atuais, aceitas nas perguntas 1339 e 1342. Os prompts ficam em `tmp/arte/cascata/prompt.md`.

| Peça | Como é |
|---|---|
| Cubit Casing | O corpo branco com as antenas e os ombros, e o buraco do rosto aberto |
| Cubit Visor | A tela escura com os olhos ciano e a linha laranja |
| Cubit Treads | O par de esteiras escuras ligadas pelo eixo |
| Neural Core | Um chip escuro com pinos nos quatro lados e uma rede neural em ciano |
| Power Bank Casing | A Base Power Bank vazia, sem LEDs, sem faixa e com a alça cinza |
| Power Cells | Uma célula branca com a faixa na cor do tier. A Ultimate tem o corpo de netherite e a faixa dourada |

## Contrato

| Onde | Nome | Conteúdo |
|---|---|---|
| Item | `cubit:general_robot_casing`, `cubit:general_robot_visor`, `cubit:general_robot_treads` | As peças só do Cubit. O Neural Core, `cubit:neural_core`, é compartilhado |
| Item | `cubit:power_bank_casing` | A carcaça das Power Banks |
| Item | `cubit:base_power_cell`, `cubit:advanced_power_cell`, `cubit:elite_power_cell` e `cubit:ultimate_power_cell` | As células |
| Receita | Os mesmos IDs das peças | A receita de cada peça |
| Receita | `cubit:general_robot` | Passa de receita com forma para sem forma, com o mesmo ID |
| Receita | `cubit:base_power_bank` | Passa de receita com forma para sem forma, com o mesmo ID |
| Receita | `cubit:advanced_power_bank`, `cubit:elite_power_bank` e `cubit:ultimate_power_bank` | O upgrade, no mesmo tipo `cubit:power_bank_upgrade`, com a cruz |
| Receita | `cubit:advanced_power_bank_from_casing`, `cubit:elite_power_bank_from_casing` e `cubit:ultimate_power_bank_from_casing` | A Power Bank do zero, sem forma |
| Tradução | `item.cubit.{id}` | Em inglês, como na tabela de IDs, e em português: Carcaça do Cubit, Visor do Cubit, Esteiras do Cubit, Carcaça da Power Bank e Célula de Energia Base, Avançada, Elite e Suprema |

As peças não têm data components nem NBT. Só os dois IDs da pergunta 2166 mudaram.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-24 11:44 | Acompanha o Neural Core, a Circuit Board, os cards e a Power Bank Casing no código (perguntas 2162 a 2173). |
| Zennd | 2026-11-24 11:33 | Registra a autorização do ajuste do Neural Core, dos cards e da Power Bank (pergunta 2170). |
| Zennd | 2026-11-24 11:32 | Acrescenta o Neural Core, os cards e a Power Bank Casing com as peças (perguntas 2162 a 2169). |
| Zennd | 2026-11-24 11:10 | Registra as texturas, o nome da categoria e a barra do catálogo (perguntas 2156 a 2160). |
| Zennd | 2026-11-24 10:50 | Registra a autorização das peças compartilhadas (pergunta 2155). |
| Zennd | 2026-11-24 10:46 | Acrescenta o conceito das peças compartilhadas (perguntas 2118 e 2131 a 2152). |
| Zennd | 2026-11-21 13:40 | Libera o Cubit e as peças desde o início (pergunta 1496). |
| Zennd | 2026-11-21 12:50 | Passa as peças para a Engineering Desk (perguntas 1427 e 1441). |
| Marcelo Fabiano | 2026-11-21 02:40 | Leva os Modules para a linha das células (perguntas 1357 e 1358). |
| Marcelo Fabiano | 2026-11-21 01:45 | Encadeia as células e troca o upgrade pela cruz (perguntas 1354 e 1355). |
| Marcelo Fabiano | 2026-11-21 00:55 | Cria o conceito das peças de montagem (perguntas 1338 a 1351). |
