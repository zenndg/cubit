# Patrulheiro Galático

Conceito do trabalho de Patrulheiro Galático, que substitui o Defensor (#198). O documentado pode mudar se aparecer um impedimento técnico (regra 49 do `AGENTS.md`). Ele complementa [`trabalhos.md`](../trabalhos.md), cujas regras de toda área e a [base do trabalho](../trabalhos.md#base-do-trabalho) valem aqui, com as exceções escritas abaixo.

**Situação:** autorizado (perguntas 1591 e 1613). O conceito vem do desenvolvedor (perguntas 1550 a 1552) e foi refinado nas perguntas 1553 a 1612. O PR 1, com o comportamento, entrou no #238, e o PR 2 traz a rota. A prioridade é a mais alta, como a da Engineering Desk: cada detalhe do conceito e da aparência é decidido antes do código (pergunta 1560). Os números ficam em [`tiers.md`](../../tiers.md#trabalhos).

## A ideia

O Patrulheiro Galático é o Cubit nerd, fã de ficção científica, que leva a sério a missão de proteger o player e a base. Ele faz a ronda, procura hostis e ataca primeiro, de pistola laser numa garra e lâmina de plasma na outra.

Onde o Defensor só protegia uma área, o Patrulheiro também vai atrás: percorre uma rota, persegue o hostil até um limite e volta ao ponto onde parou. Com o card, o Cubit bate mais forte e aguenta mais que um Cubit sem ele (pergunta 1552).

O visual é uma homenagem ao clima de aventura espacial, com design todo próprio: nenhuma peça copia personagem, arma ou marca de filme (regra 9). As asas abrem para trás, como uma asa-delta, e a lâmina sai direto da garra, sem cabo (perguntas 1553 e 1554).

## Imagem conceito

![O Cubit Patrulheiro Galático](../../imagens/conceito/trabalhos/patrulheiro.png)

Aceita na pergunta 1592 como referência, com dois ajustes aplicados direto no modelo:

1. **Asas recolhidas:** deitadas sobre a mochila, com as pontas passando da borda de trás. No painel da mochila, elas não aparecem.
2. **Asas abertas:** mais para trás que para os lados, como na figura principal, e nunca no "V" invertido do painel da mochila (pergunta 1553).

O nome é "Galático", nos documentos e no jogo, e não "Galáctico", como no título da imagem.

## Card

| Nome | ID | Descrição |
|---|---|---|
| Machine Learning: Galactic Patroller | `cubit:ml_patroller` | Card de Aprendizado de Máquina do trabalho de Patrulheiro Galático. Em português, "Patrulheiro Galático" |

Segue as regras dos outros cards: slot exclusivo, um por Cubit, sai sem perda e só trabalha em área marcada (pergunta 1599). Disponível a partir do Elite, no lugar do Defensor (perguntas 1150, 1539 e 1563). Substitui `cubit:ml_defender` e o valor `defender` do `allowed_jobs`, que não foram publicados, sem migração (pergunta 1569).

### Receita

Sem forma, rendendo 1, como todos os cards: a Card Casing, a Circuit Board e uma estrela de fogo de artifício, que lembra o distintivo da estrela dourada. Libera com o Elite Module (perguntas 1544, 1546 e 1576).

## Como o player o põe para patrulhar

1. Põe o card no slot de um Cubit Elite ou Ultimate.
2. Com o app Área do Tablet, marca a área que ele guarda, que pode ter o dobro do lado da área dos outros cards: até 64×64 no Elite e 128×128 no Ultimate, com a altura do tier, de 24 e 32 blocos (perguntas 1600 e 1601). Sem área, ele segue o dono e faz a escolta (perguntas 1565 e 1599).
3. Com o app Marcar, marca os pontos da rota dentro da área, na ordem em que quer que ele passe (perguntas 1579 e 1599).
4. Sem pontos marcados, ele patrulha a área pelas bordas, no mesmo passo lento, parando em cada canto e a cada 16 blocos andados (perguntas 1561 e 1616).
5. Para guardar os drops, marca um baú com o app Descarga, como nos outros cards (pergunta 1570).

### A rota

- **Marcar:** com o card do Patrulheiro, o app Marcar marca o topo de qualquer bloco sólido dentro da área, na ordem dos cliques, até 16 pontos no Elite e 32 no Ultimate. O clique num ponto marcado o desmarca, e Shift + clique desmarca o ponto clicado ou, fora de um ponto, o último, como hoje. A ordem dos outros pontos se mantém (perguntas 1579 e 1611).
- **Avisos:** na barra de ação, sem área, `Mark the area first with the Area app`; fora da área, `Only inside the area of %s`; num bloco que não é sólido, `Mark the top of a solid block`; e ao marcar, `Point %s of %s marked for %s`, com o número do ponto, o limite e o nome do Cubit. Desmarcar, rota cheia e nenhum ponto usam os avisos de hoje do app Marcar (pergunta 1611).
- **Ver:** com o Tablet na mão e o app Marcar aberto, cada ponto da rota do Cubit escolhido mostra uma seta com o número da ordem, e uma linha ciano tracejada liga os pontos em laço. Só a rota do Cubit escolhido aparece, para os números de rotas diferentes não se misturarem (perguntas 1580 e 1612).
- **Ritmo:** ele percorre os pontos em laço, num passo de vigia, a 60% da velocidade normal. A perseguição e a volta à rota seguem na velocidade normal (pergunta 1614).
- **Paradas:** ele para em cada ponto e a cada 16 blocos andados no caminho, e o contador zera a cada parada. Em cada parada, fica 4 s, vira a cabeça devagar para a esquerda e para a direita duas vezes, volta ao centro e retoma a caminhada. Um combate interrompe a parada (perguntas 1615, 1617 e 1618).
- **Guardar:** os pontos ficam salvos no NBT do Cubit, mesmo sem o card, e voltam a valer quando o card volta. Ao recolher o Cubit, eles se perdem, como a área e as marcas do Organizador (perguntas 1582 e 1602).
- **Chunks:** ele nunca entra em chunk descarregado. Um ponto num chunk descarregado é pulado até carregar de novo (regra 22).

## Combate

### Quem ele ataca

- **Modo:** só o Agressivo, enquanto tiver o card, porque atacar primeiro é a essência dele. A aba Defense mostra o modo travado como um modo bloqueado pelo tier: o Agressivo marcado, e o Pacífico e o Contra-atacar escurecidos, com `Locked by the Galactic Patroller card` no lugar do detalhe. Ao tirar o card, volta o modo que estava antes (perguntas 1566 e 1610).
- **Procura:** ataca qualquer mob hostil no raio de procura, em volta de onde está, e todo mob, hostil ou neutro, que ferir o próprio Cubit ou um protegido.
- **Protegidos:** o dono, os outros players, os robôs, os animais e os aldeões. Ele nunca os fere, e o globo e a lâmina passam por eles (perguntas 1558 e 1570).
- **Prioridade:** primeiro o mob que está ferindo alguém, depois o mais próximo de um protegido (pergunta 1568).
- **Vários Patrulheiros:** cada um reserva um hostil, e os outros escolhem outro. Com um só hostil, todos atacam o mesmo (pergunta 1568).

### Perseguição

Ele sai da rota para ir atrás de um hostil, até 16 blocos dela, e volta ao ponto onde parou quando o alvo morre ou foge do limite. Nunca entra em chunk descarregado (perguntas 1562 e regra 22).

### Escolta

Seguindo o player, ele não patrulha, mas usa a pistola e a lâmina no lugar do choque para proteger o player, no modo Agressivo. A rota volta quando ele deixa de seguir. É a exceção à regra de que todo card age como generalista ao seguir (pergunta 1565).

### Choque

Com o card, a pistola e a lâmina fazem o papel do choque. Sem o card, o choque volta (pergunta 1556).

### Ataque e defesa maiores

Enquanto o card está no slot, o Cubit ganha armadura extra, pelo atributo do jogo, e as armas batem acima do choque do mesmo tier. O bônus sai junto com o card, e a vida do tier não muda (perguntas 1552, 1555 e 1577).

### Creeper

Contra o creeper, usa sempre a pistola, nunca a lâmina. Se o creeper começar a acender perto dele ou de um protegido, ele recua até sair do raio da explosão e volta a atirar (pergunta 1567). Sem carga para atirar, ele não ataca o creeper: fica fora do raio, entre o creeper e os protegidos, e volta a atirar quando tiver carga (pergunta 1595).

## Armas

As duas armas são partes do modelo do Cubit com o card, sem item para o player (pergunta 1573). A pistola fica na garra esquerda e o emissor da lâmina no pulso direito, o tempo todo (pergunta 1574). Ele escolhe a arma sozinho, pela distância: a pistola a partir de 4 blocos e contra alvos no ar, e a lâmina mais perto (pergunta 1557).

### Pistola laser

- **Forma:** branca, com o cano em ciano, faixas de energia acesas no corpo e um botão laranja.
- **Disparo:** um globo de laser azul, da cor dos olhos do Cubit, que voa em linha reta, sem gravidade, e acerta só o mob atingido (pergunta 1558).
- **Estouro:** ao acertar, estoura em partículas azuis, como uma poção de arremesso, lembrando uma explosão magnética. O estouro é só visual e nunca quebra blocos (perguntas 1551 e 1558).
- **Lentidão:** cada acerto tem 20% de chance de deixar o alvo lento (pergunta 1551).
- **Energia:** cada disparo gasta bateria. Sem carga para atirar, ele usa só a lâmina (pergunta 1571).

### Plasma Blade

- **Forma:** sem cabo. Um emissor hexagonal preso ao pulso, com um anel laranja, projeta a lâmina verde direto da garra, um pouco mais larga e com a ponta em cunha (pergunta 1554). O nome no jogo é "Plasma Blade", e nunca "sabre de luz".
- **Acende:** a lâmina acende, com som, só no combate de perto, e apaga depois (pergunta 1574).
- **Empurrão:** cada golpe empurra o alvo para trás (pergunta 1551).
- **Cabeça:** ao matar um mob que tem cabeça no jogo (zumbi, esqueleto, creeper, piglin e wither skeleton), 50% de chance de derrubar a cabeça. Os outros hostis não derrubam nada a mais, e a cabeça vai para o inventário do Cubit, como os outros drops (pergunta 1559).
- **Energia:** a lâmina não gasta bateria, para ele nunca ficar sem defesa (pergunta 1571).

## Asas

- **Abrem:** na queda e no salto, para trás e para baixo, como uma asa-delta, com os propulsores acesos. Fecham ao tocar o chão (pergunta 1553).
- **Planar:** na queda, ele plana, sem dano de queda e sem gastar bateria (perguntas 1564 e 1571).
- **Salto:** só no combate, para um alvo no ar ao alcance da altura do salto, quando a pistola não pode atirar, sem carga ou fora do alcance. Ele golpeia com a lâmina no ar. Com carga, atira de baixo e não salta. O Ultimate salta mais alto, e cada salto gasta bateria (perguntas 1564 e 1596).
- **Mobs voadores:** a pistola alcança a maior parte, e o salto aproxima o Cubit de um Phantom ou de um Blaze para a lâmina quando a pistola não pode atirar (pergunta 1596).

## Drops e descarga

Guarda os drops no inventário enquanto couberem, e o resto cai no chão. Não hiberna por inventário cheio e só para sem bateria. Com baú marcado no app Descarga, descarrega quando não há ameaça (pergunta 1570).

## Evolução por tier

Os números ficam em [`tiers.md`](../../tiers.md#trabalhos), todos no config (pergunta 1577).

| Recurso | Elite | Ultimate |
|---|---|---|
| Pistola: dano | 5 | 7 |
| Pistola: alcance | 16 blocos | 24 blocos |
| Pistola: intervalo | 1 s | 0,6 s |
| Pistola: custo por disparo | 1.000 FE | 800 FE |
| Lentidão, com 20% de chance | I, por 3 s | II, por 3 s |
| Lâmina: dano | 7 | 10 |
| Lâmina: intervalo | 0,6 s | 0,5 s |
| Lâmina: empurrão | Médio | Forte |
| Cabeça, ao matar com a lâmina | 50% | 50% |
| Armadura extra | +6 | +10 |
| Raio de procura | 16 blocos | 24 blocos |
| Perseguição máxima | 16 blocos | 16 blocos |
| Troca de arma | 4 blocos | 4 blocos |
| Salto: altura | 3 blocos | 5 blocos |
| Salto: custo | 500 FE | 400 FE |
| Pontos da rota | 16 | 32 |
| Lado da área | 64 blocos | 128 blocos |
| Velocidade da ronda | 60% | 60% |
| Parada | 4 s | 4 s |
| Blocos até a próxima parada | 16 | 16 |

## Visual

| Peça | Como é |
|---|---|
| Capacete | Branco, cobre o topo da cabeça e desce pela lateral. Tem um filete ciano, a estrela dourada no alto e uma dobradiça de cada lado, com o centro laranja das antenas. As antenas saem por cima dele |
| Visor | Lente ciano translúcida, presa às dobradiças, que se curva pela frente e pela lateral. Baixado, cobre o alto da tela e mostra uma mira. Erguido, fica recolhido no topo do capacete |
| Tela | Com o visor erguido, os olhos normais do Cubit. Com o visor baixado, olhos decididos e marcas de mira nos cantos |
| Mochila | Branca, com um filete ciano no topo, um painel escuro com barras de carga atrás e três propulsores embaixo |
| Asas | Saem da mochila por uma dobradiça mecânica escura com centro laranja. Cada asa tem a lâmina principal varrida para trás, com a borda da frente curva, a de trás reta e uma espessura escura embaixo; um LED ciano na borda da frente; painéis divididos por linhas finas; uma aleta cinza embaixo, com três luzes ciano; e uma cápsula branca com janela ciano na ponta. Recolhidas, ficam deitadas sobre a mochila. Abertas, vão para trás e para baixo, como uma asa-delta (pergunta 1553) |
| Pistola | Na garra esquerda, branca, com cano ciano, faixas de energia acesas e um botão laranja |
| Plasma Blade | No pulso direito, o emissor hexagonal com anel laranja. A lâmina verde, larga e com ponta em cunha, sai direto da garra (pergunta 1554) |
| Distintivo | Escudo escuro com a estrela dourada, na lateral do corpo |
| Coleira | A cor do tier, como nas outras profissões |

### Combate e patrulha

- **Combate:** o visor desce, os olhos ficam decididos, e a lâmina acende quando o alvo chega perto. O player vê o modo de combate só de olhar para ele.
- **Patrulha:** o visor sobe, e ele percorre a rota olhando em volta.

## Falas

Como todas as profissões, ele fala uma de três frases ao receber o card e frases próprias ao entrar em combate e ao abater um hostil, como um herói espacial e um cavaleiro sereno, sem citar filmes. As frases e as regras estão em [`falas.md`](../../conceito/falas.md#profissões) (perguntas 1583 a 1590).

## Sons

Sons próprios em `.ogg`, descritos em [`sons.md`](../../conceito/sons.md#patrulheiro-galático): o agente descreve cada um, com a duração e o clima, o desenvolvedor fornece os `.wav`, e o agente os trata (pergunta 1575).

## Fora do escopo

- **DDoS e Firewall:** o conceito original cita o "time do mal", que aparece à noite, e o Firewall. Eles não estão no roadmap, e o Patrulheiro não depende deles. Um bônus à noite e a combinação com o Firewall ficam para quando esses cards forem planejados (pergunta 1572 e regra 43).
- **Armas para o player:** a pistola e a lâmina não viram itens (pergunta 1573).
- **Atravessar buracos:** o salto não atravessa buracos na ronda, e o caminho contorna os buracos, como hoje. Fica para um card futuro (pergunta 1597).

## Contrato

| Onde | Nome | Conteúdo |
|---|---|---|
| Item | `cubit:ml_patroller` | O card, na tag `cubit:ml_cards`. Substitui `cubit:ml_defender`, que não foi publicado (pergunta 1569) |
| Receita | `cubit:ml_patroller` | Card Casing, Circuit Board e estrela de fogo de artifício, liberada pelo Elite Module (pergunta 1576) |
| Config, `allowed_jobs` | `patroller` | No lugar de `defender`, no Elite e no Ultimate (pergunta 1569) |
| Config, por tier | `patroller_pistol_damage`, `patroller_pistol_range`, `patroller_pistol_interval_ticks`, `patroller_pistol_cost`, `patroller_slowness_level`, `patroller_blade_damage`, `patroller_blade_interval_ticks`, `patroller_blade_knockback`, `patroller_armor`, `patroller_search_radius`, `patroller_jump_height`, `patroller_jump_cost`, `patroller_route_points` e `patroller_area_size` | Os números por tier da tabela acima |
| Config, geral | `patroller_slowness_chance`, `patroller_slowness_ticks`, `patroller_head_chance`, `patroller_chase_distance`, `patroller_weapon_switch_distance`, `patroller_stop_ticks`, `patroller_stop_distance` e `patroller_patrol_speed` | 20%, 3 s, 50%, 16 blocos, 4 blocos, 4 s, 16 blocos e 0,6 (perguntas 1614 a 1618) |
| NBT da entidade | `PatrolRoute` | Os pontos da rota, em ordem. Sem a chave no save, a rota é vazia. Não vai para o item (perguntas 1582 e 1602) |
| Entidade | `cubit:laser_orb` | O globo de laser da pistola |
| Tipos de dano | `cubit:laser_orb` e `cubit:plasma_blade` | O dano das duas armas, com as mensagens de morte em inglês e em português |
| Pacote do servidor | `cubit:patrol_route` | Os pontos da rota do Cubit escolhido no Tablet, para as setas e a linha do app Marcar (pergunta 1612) |
| Sons | Os eventos de `sons.md` | Disparo, estouro, lâmina, golpe, asas e visor |
| Config, geral | `job_chatter_chance`, `job_chatter_interval_ticks` e `job_chatter_range` | As falas de todas as profissões: 20%, 2 min e 32 blocos (perguntas 1585 e 1586) |
| Traduções | As frases de `falas.md` | As falas de entrada e do trabalho das cinco profissões, em inglês e português |

## Entrega

Uma issue, a #198 renomeada, e três PRs testados no jogo (perguntas 1578 e 1605):

1. **Comportamento:** as falas das cinco profissões, o card, a troca do Defensor, os tiers dos trabalhos, a ronda pelas bordas da área, a perseguição, os alvos, a escolta, o modo Agressivo no servidor, as armas com o dano, a lentidão, o empurrão e a cabeça, a armadura, o gasto, o planar, o salto, a receita e os GameTests, com um visual provisório.
2. **Rota:** o modo travado na aba Defense, com o motivo, a rota no app Marcar, com as setas numeradas, a linha tracejada e o pacote `cubit:patrol_route`, e os GameTests da rota.
3. **Visual:** o modelo, as texturas, as animações, os efeitos e os sons, a partir das imagens e dos `.wav` aprovados.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-22 16:30 | Atualiza a numeração das regras do `AGENTS.md` (pergunta 1716). |
| Zennd | 2026-11-22 11:00 | Troca a pausa nos pontos pela ronda lenta, com paradas nos pontos, nos cantos e a cada 16 blocos (perguntas 1614 a 1618). |
| Zennd | 2026-11-22 10:40 | Atualiza a situação do conceito (pergunta 1613). |
| Zennd | 2026-11-22 09:50 | Detalha a aba Defense travada, os cliques e os avisos do app Marcar e a rota só do Cubit escolhido (perguntas 1610 a 1612). |
| Zennd | 2026-11-22 09:30 | Divide a entrega em três PRs (pergunta 1605). |
| Zennd | 2026-11-22 08:40 | Deixa a rota só no NBT, sem o componente do item (pergunta 1602). |
| Zennd | 2026-11-22 08:10 | Dobra o lado da área do Patrulheiro (pergunta 1600). |
| Zennd | 2026-11-22 08:08 | Registra o creeper sem carga, o salto, a área obrigatória e o tier dos trabalhos (perguntas 1595 a 1599). |
| Zennd | 2026-11-21 23:22 | Acrescenta a imagem conceito aceita, com os ajustes das asas (pergunta 1592). |
| Zennd | 2026-11-21 23:30 | Cria o conceito do Patrulheiro Galático, no lugar do Defensor, a partir do conceito do desenvolvedor (perguntas 1550 a 1599). |
