# Sons

Os arquivos ficam em `src/main/resources/assets/cubit/sounds/general_robot/`. Quem ouve, os gatilhos e o formato estão em [`visual.md`](visual.md#sons).

| Arquivo | Item | Para que é usado |
|---|---|---|
| `tracks.ogg` | Cubit | Esteiras ao andar, em loop, acompanhando a velocidade |
| `hostile_mob_alert.ogg` | Cubit | Alerta ao encontrar um mob hostil, mais alto que o original (pergunta 571) |
| `rare_ore_alert.ogg` | Cubit | Alerta ao encontrar um minério raro, mais alto que o original (pergunta 571) |
| `expression_beep.ogg` | Cubit | Bipe ao trocar para uma expressão sem som próprio |
| `short_circuit.ogg` | Cubit | Curto elétrico, em loop, enquanto pega fogo, e uma vez ao levar dano |
| `power_on.ogg` | Cubit | Ao passar de desligado a ligado |
| `power_off.ogg` | Cubit | Ao passar de ligado a desligado |
| `recharging.ogg` | Cubit | Em loop, enquanto a bateria sobe, na base ou pelo slot de energia |
| `shock_attack.ogg` | Cubit | Choque ao atacar |
| `placed.ogg` | Cubit | Ao ser colocado no mundo |
| `aggressive_mode.ogg` | Cubit | Ao trocar para o modo agressivo |

## Engineering Desk

Os arquivos ficam em `src/main/resources/assets/cubit/sounds/engineering_desk/`, a partir dos `.wav` fornecidos pelo desenvolvedor, convertidos para Ogg, com os originais em `tmp/wav/` (perguntas 1451, 1452 e 1477). A mesa é o primeiro contato do player com o mod, e os sons seguem o mesmo cuidado do visual (pergunta 1463).

| Arquivo | Para que é usado | Duração e clima |
|---|---|---|
| `crafting.ogg` | Em loop, enquanto a mesa cria um item. Para na pausa e no fim do pedido | 2 a 4 s, sem emenda audível no loop. Baixo e agradável: um zumbido eletrônico suave, com cliques leves do braço mecânico e de teclas, que não cansa depois de minutos |
| `done.ogg` | Quando cada item fica pronto | Menos de 1 s. Um "plim" eletrônico curto e satisfatório, com um leve estalo de faísca e o clique da gaveta fechando |
| `startup.ogg` | Na animação de ligação, ao colocar a mesa | 1 a 2 s. Um bipe de ligação simpático, da família dos bipes do Cubit, subindo de tom, como um computador fofo acordando |

## Patrulheiro Galático

Os arquivos ficam em `src/main/resources/assets/cubit/sounds/general_robot/patroller/`, a partir dos `.wav` fornecidos pelo desenvolvedor, convertidos para Ogg, com os originais em `tmp/wav/patrulheiro/` (pergunta 1575). O card tem a prioridade mais alta, e os sons seguem o mesmo cuidado do visual (pergunta 1560). Todos têm o tom de ficção científica alegre do Cubit: nada de tiro realista nem de zumbido que lembre o sabre de luz.

| Arquivo | Para que é usado | Duração e clima |
|---|---|---|
| `pistol_shot.ogg` | Cada disparo da pistola laser | Menos de 0,5 s. Um "piu" eletrônico curto e brilhante, subindo de tom, com um leve chiado de carga no começo. Várias variações de tom no `sounds.json`, para os disparos seguidos não cansarem |
| `orb_burst.ogg` | O estouro do globo de laser no alvo | Menos de 0,7 s. Um estalo magnético abafado, como uma bolha de energia estourando, com um eco curto metálico. Lembra uma explosão magnética, sem estrondo |
| `blade_ignite.ogg` | A Plasma Blade acendendo, ao entrar no combate de perto | 0,5 a 1 s. Um "fuuum" elétrico subindo, que termina num chiado estável |
| `blade_swing.ogg` | Cada golpe da lâmina | Menos de 0,5 s. Um sopro elétrico rápido, que passa da esquerda para a direita. Variações de tom no `sounds.json` |
| `blade_hit.ogg` | A lâmina acertando um mob, com o empurrão | Menos de 0,5 s. Um estalo de energia curto e encorpado, com um leve crepitar |
| `blade_off.ogg` | A lâmina apagando, ao sair do combate | Menos de 0,7 s. O "fuuum" ao contrário, descendo, e um clique do emissor |
| `wings_open.ogg` | As asas abrindo, no salto ou na queda | Menos de 1 s. Um clique mecânico duplo das dobradiças, seguido do sopro dos propulsores acendendo |
| `wings_close.ogg` | As asas fechando ao tocar o chão | Menos de 0,5 s. O clique das dobradiças e o sopro apagando |
| `jump.ogg` | O salto com as asas | Menos de 0,7 s. Um impulso de propulsor curto e grave, como um jato pequeno |
| `visor_down.ogg` | O visor baixando, ao entrar em combate | Menos de 0,5 s. Um deslize de servo e um bipe de mira travada |
| `visor_up.ogg` | O visor subindo, ao voltar à patrulha | Menos de 0,5 s. O deslize de servo ao contrário e um bipe calmo |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-21 23:02 | Descreve os sons do Patrulheiro Galático (pergunta 1575). |
| Zennd | 2026-11-21 12:30 | Descreve os sons da Engineering Desk (perguntas 1451, 1452 e 1477). |
| Marcelo Fabiano | 2026-11-07 16:27 | Registra o volume maior dos alertas. |
| Marcelo Fabiano | 2026-11-06 00:12 | Adiciona o uso do curto elétrico ao levar dano. |
| Marcelo Fabiano | 2026-11-05 23:16 | Adiciona o som de modo agressivo. |
| Marcelo Fabiano | 2026-11-05 22:47 | Cria a lista dos sons. |
