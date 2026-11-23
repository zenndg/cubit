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

## Induction Dock

Os arquivos ficam em `src/main/resources/assets/cubit/sounds/induction_dock/`, a partir dos `.wav` fornecidos pelo desenvolvedor, convertidos para Ogg, mono e 48 kHz, com os originais em `tmp/wav/dock/` (perguntas 1490, 1491 e 1861). Os IDs são `cubit:induction_dock.{nome}`, com as legendas em inglês e português, ouvidos até 16 blocos, como os outros sons do mod. O Dock é um dos itens mais importantes do mod, e os sons seguem o cuidado do visual (pergunta 1853).

| Arquivo | Para que é usado | Duração e clima |
|---|---|---|
| `panel_deploy.ogg` | A montagem do painel: as portas deslizando, os braços saindo e subindo e as metades deitando (passos 1 a 4 da montagem) | Cerca de 3 s. Um mecanismo preciso e bem lubrificado: o deslize suave das portas, o zumbido de servos subindo de tom enquanto os braços sobem, e um leve assentar das metades. Tecnológico e caprichado, sem rangido nem pancada |
| `panel_lock.ogg` | As duas metades se unindo no centro, com a trava (passo 5) | Menos de 0,5 s. Um clique metálico firme e satisfatório, com um bipe curto de confirmação, da família dos bipes do Cubit |
| `panel_retract.ogg` | A desmontagem do painel, do destravar ao fechar das portas | Cerca de 3,5 s. O destravar curto, o zumbido dos servos descendo de tom, as metades recolhendo e o deslize das portas fechando, terminando num assentar macio |

## Patrulheiro Galático

Os arquivos ficam em `src/main/resources/assets/cubit/sounds/general_robot/patroller/`, a partir dos `.wav` fornecidos pelo desenvolvedor, convertidos para Ogg, com os originais em `tmp/wav/patrulheiro/` (pergunta 1575). O card tem a prioridade mais alta, e os sons seguem o mesmo cuidado do visual (pergunta 1560). Todos têm o tom de ficção científica alegre do Cubit: nada de tiro realista nem de zumbido que lembre o sabre de luz.

- **IDs:** `cubit:general_robot.patroller.{nome}`, com o nome do arquivo sem a extensão, e as legendas em `subtitles.cubit.general_robot.patroller.{nome}`, em inglês e português (pergunta 1791).
- **Onde tocam:** todos no servidor. O visor, a lâmina e as asas tocam na troca do estado, também ao tirar o card, quando tudo se recolhe. O disparo, o golpe, o acerto, o estouro e o impulso tocam na própria ação (perguntas 1792, 1797 e 1799).
- **Volume e alcance:** o nível dos arquivos definitivos, no volume normal do jogo, até 16 blocos, como os outros sons do mod (perguntas 1795 e 1796).
- **Arquivos:** os originais definitivos ficam em `tmp/wav/patrulheiro/sintetizados/` (pergunta 1790).

| Arquivo | Para que é usado | Duração e clima |
|---|---|---|
| `pistol_shot.ogg` | Cada disparo da pistola laser | Menos de 0,5 s. Um "piu" eletrônico curto e brilhante, subindo de tom, com um leve chiado de carga no começo. Três tons no `sounds.json`, 0,9, 1,0 e 1,1, sorteados pelo jogo, para os disparos seguidos não cansarem (pergunta 1793) |
| `orb_burst.ogg` | O estouro do globo de laser, no mob ou num bloco (pergunta 1798) | Menos de 0,7 s. Um estalo magnético abafado, como uma bolha de energia estourando, com um eco curto metálico. Lembra uma explosão magnética, sem estrondo |
| `blade_ignite.ogg` | A Plasma Blade acendendo, com o alvo a 8 blocos (pergunta 1804) | 0,5 a 1 s. Um "fuuum" elétrico subindo, que termina num chiado estável |
| `blade_swing.ogg` | Cada golpe da lâmina | Menos de 0,5 s. Um sopro elétrico rápido, em mono, como os outros, porque o jogo só diminui com a distância o volume de um som mono (pergunta 1794). Três tons no `sounds.json`, 0,9, 1,0 e 1,1 (pergunta 1793) |
| `blade_hit.ogg` | A lâmina acertando um mob, com o empurrão | Menos de 0,5 s. Um estalo de energia curto e encorpado, com um leve crepitar |
| `blade_off.ogg` | A lâmina apagando, 2 s depois que o alvo se afasta ou cai, ou na hora, ao tirar o card (perguntas 1799 e 1805) | Menos de 0,7 s. O "fuuum" ao contrário, descendo, e um clique do emissor |
| `wings_open.ogg` | As asas abrindo, no salto, no pulo de asa, no vão, na descida e na queda (perguntas 1647 a 1649) | Menos de 1 s. Um clique mecânico duplo das dobradiças, seguido do sopro dos propulsores acendendo |
| `wings_close.ogg` | As asas fechando, 2 s depois do pouso ou do fim do voo, ou na hora, ao tirar o card (perguntas 1799 e 1807) | Menos de 0,5 s. O clique das dobradiças e o sopro apagando |
| `jump.ogg` | O impulso das asas, no salto do combate, no pulo de asa e no início de um vão. A descida e a queda não têm impulso | Menos de 0,7 s. Um impulso de propulsor curto e grave, como um jato pequeno |
| `visor_down.ogg` | O visor baixando, no alerta ao avistar um hostil, antes do primeiro ataque (pergunta 1623) | Menos de 0,5 s. Um deslize de servo e um bipe de mira travada |
| `visor_up.ogg` | O visor subindo, depois da investigação ou de 3 s sem ameaça (pergunta 1629) | Menos de 0,5 s. O deslize de servo ao contrário e um bipe calmo |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-22 23:45 | Descreve os três sons do painel do Dock (perguntas 1853 e 1861). |
| Zennd | 2026-11-22 22:00 | Toca o fechamento das asas no fim do tempo em que ficam abertas (pergunta 1807). |
| Zennd | 2026-11-22 21:46 | Liga os sons da lâmina à distância em que ela acende e ao tempo em que continua acesa (perguntas 1804 e 1805). |
| Zennd | 2026-11-22 21:31 | Registra os IDs, os tons, o mono do golpe, o estouro num bloco, onde tocam e o volume dos sons do Patrulheiro (perguntas 1790 a 1799). |
| Zennd | 2026-11-22 13:00 | Liga os sons das asas e do visor ao pulo de asa, ao alerta e ao visor com calma (perguntas 1623, 1629 e 1647 a 1649). |
| Zennd | 2026-11-21 23:02 | Descreve os sons do Patrulheiro Galático (pergunta 1575). |
| Zennd | 2026-11-21 12:30 | Descreve os sons da Engineering Desk (perguntas 1451, 1452 e 1477). |
| Marcelo Fabiano | 2026-11-07 16:27 | Registra o volume maior dos alertas. |
| Marcelo Fabiano | 2026-11-06 00:12 | Adiciona o uso do curto elétrico ao levar dano. |
| Marcelo Fabiano | 2026-11-05 23:16 | Adiciona o som de modo agressivo. |
| Marcelo Fabiano | 2026-11-05 22:47 | Cria a lista dos sons. |
