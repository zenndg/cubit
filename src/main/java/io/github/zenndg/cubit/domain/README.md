# Domínio

Regras do Cubit em Java puro, sem depender do jogo (regra 12 do `AGENTS.md`). Quem chama fornece os dados: o domínio não lê config, relógio nem o mundo.

## Linguagem

| Termo | Significado |
|---|---|
| `Energy` | Quantidade inteira de FE, nunca negativa |
| `EnergyReading` | Uma carga lida contra a capacidade, para mostrar em porcentagem e FE, como no Jade, sem falso zero |
| `Percentage` | Porcentagem inteira, de 0 a 100 |
| `Battery` | Capacidade e carga atual. É imutável, e cada operação devolve uma bateria nova |
| `PowerState` | Estado do robô |
| `PowerRules` | Regras que mudam o estado por causa da energia, e o desligar pela tela, só do robô ativo |
| `RobotColor` | A cor da carcaça, uma para cada corante do jogo. A `key` é o nome do corante, usado nos saves e nas texturas |
| `PaintRules` | Quando um corante muda o robô e quando ele reage |
| `RobotType` | Identifica o Cubit nos IDs e no config. O robô é único, e é um dado, identificado por um ID |
| `RobotTier` | Nível de evolução: Base, Advanced, Elite e Ultimate. A `key` é o nome usado no config e nos saves. `next` é o tier seguinte, e `previous`, o que um Module desse tier exige |
| `TierUpgrade` | O que um Module faz no robô: sobe o tier, pede o tier anterior primeiro ou encontra o robô já nesse tier ou acima |
| `RobotState` | O que o robô guarda ao virar item: tier, energia, vida, a chave da lanterna, a cor, a chave dos scanners, o filtro de minérios, a chave das falas, o modo de defesa, a chave do painel solar, a coleta e as chaves das tochas, do retorno automático, da descarga e do local de morte, a velocidade e se o Cubit já se apresentou (`introduced`). O `initial` nasce sem a apresentação, e o que foi salvo antes dela conta como apresentado (perguntas 1679 e 1750). `damaged` e `repaired` dão o estado ao morrer e ao consertar |
| `TierSpec` | Números de um tipo de robô num tier |
| `ShockSpec` | O choque de um tier: dano, alcance, intervalo, custo, se atinge todos no alcance e os modos de defesa liberados. O Pacífico sempre está liberado. `NONE` é o tier sem choque |
| `DefenseMode` | Quem o robô ataca: Pacífico, Contra-atacar, Agressivo ou Focus. A `key` é o nome usado no config e nos saves, e `INITIAL` é o Pacífico |
| `PickupSpec` | A coleta de um tier: o raio do ímã, com 0 para bloqueado, o custo por item puxado, os slots e as tags do filtro de coleta e o raio da busca em volta do dono, com 0 para bloqueado. `NONE` é o tier sem esses números |
| `ShockCandidate` | O que é um ser vivo perto do robô, para o choque: o dono, um player, um robô, ou um mob pacífico, neutro ou hostil. A camada do jogo classifica |
| `DefenseRules` | Quem cada modo de defesa ataca |
| `ShockSignals` | O que o robô sabe de um ser vivo além do que ele é: se está agressivo com o dono, se é o atacante lembrado, se é o alvo do Focus e se o Focus tem alvo |
| `FocusTarget` | O alvo do Focus: o último mob que o dono feriu, até morrer, mudar de dimensão ou ficar longe do dono |
| `RetaliationMemory` | O último mob que feriu o robô ou o dono, até quando o Contra-atacar lembra dele |
| `ShockRules` | Quando o robô dá o choque, em quais alvos e até quando fica ocupado com ele |
| `ShockGaze` | Para onde o robô olha depois do choque: o alvo enquanto ele vive e está perto, e o último ponto dele por um tempo |
| `PickupRules` | Quando o robô coleta por contato e quando o ímã puxa e cobra |
| `FetchRules` | Quando o robô que segue busca os itens do chão, qual item, e quando desiste |
| `TorchSpec` | As tochas de um tier: o custo por tocha e a luz de bloco que o robô garante, com 0 para bloqueado |
| `CookSpec` | Os fornos marcados, o custo por pilha movida, a pausa e o intervalo da varredura do Cozinheiro em cada tier (pergunta 1326) |
| `OrganizerSpec` | Os baús, as marcas, o custo por pilha movida, a pausa e o intervalo da varredura do Organizador em cada tier (pergunta 1300) |
| `UnloadSpec` | Os baús que um tier pode marcar e a distância até eles (pergunta 1094) |
| `AreaSpec` | A maior área de um tier: o lado do retângulo em volta dela e a altura, com 0 para o tier sem modo área (perguntas 342, 1050 e 2350) |
| `TorchRules` | Quando o robô põe uma tocha e quais lugares são perigosos |
| `DoorRules` | Quando o Cubit abre uma porta marcada, com o custo dela, e quando a fecha de novo (perguntas 1366 a 1370) |
| `TorchGrid` | O espaço em volta do robô, lido uma vez por varredura: o que para a luz, onde mob pode nascer e onde uma tocha cabe |
| `GridCell` | Um bloco da `TorchGrid`, contado do canto dela |
| `TorchPlanner` | Escolhe onde a tocha deixa mais lugares perigosos seguros, simulando a luz do jogo |
| `FetchMemory` | Os itens em que o robô desistiu de buscar, cada um ignorado até um tick |
| `TierCatalog` | Entrega o `TierSpec` de um tipo de robô num tier. O domínio define a interface, e o config a implementa |
| `FollowAction` | O que o robô que segue faz: seguir, parar ou teleportar |
| `BurningAction` | O que o robô faz em chamas: nada, sair da lava, buscar água ou andar ao acaso |
| `FollowDecision` | Escolhe a `FollowAction` conforme o estado, a dimensão e a distância |
| `DamagingBlockReaction` | Diz se o robô sai de cima de um bloco que fere |
| `FollowingRobotPolicy` | Garante que só um robô siga o player |
| `RobotActivity` | O que o robô faz no tick: andar, peso, água, altura subida, condição negativa e lanterna acesa. Quem chama informa, e o domínio decide o gasto |
| `ConsumptionExtras` | Extras de energia de um `TierSpec`, por situação |
| `RobotExpression` | O que a tela do rosto mostra |
| `ExpressionRules` | Qual expressão vence e quando a troca toca o bipe |
| `RobotSituation` | O que vale agora para escolher a expressão |
| `ExpressionTimer` | Quanto tempo uma expressão temporária ainda dura |
| `BlinkFrame` e `BlinkCycle` | Os quadros da piscada e qual vale em cada tick |
| `GazeDirection` e `GazeTracker` | Para onde os olhos olham, com a subida e a descida de cada tick |
| `RobotPose` | A animação do corpo: desligado, andando ou parado, e se os braços ficam erguidos |
| `OwnerGaze` | Para onde os olhos olham por causa da altura do dono, e em que nível ele está |
| `BodyTilt` | Quanto o corpo inclina para o dono em outro nível |
| `TrackFrames` | Qual quadro da esteira vale em cada distância andada |
| `OwnerLook` | Quando o robô vira o corpo para o dono |
| `RobotNotice` | Os textos curtos que o robô mostra ao dono |
| `NoticeRules` | Em que estado o robô fala |
| `NoticeRepeat` | Quanto um aviso ainda espera para poder se repetir |
| `OreKind` | Um tipo de minério, pela tag: uma subtag, como `c:ores/diamond`, com nome em caixa alta, ou `c:ores`, sem nome. `shownName` dá `ORE` para ele, e é a reserva quando a tag não tem bloco, porque o aviso e a tela usam o nome do bloco no idioma do jogo (pergunta 734) |
| `OreFilter` | Os minérios que o dono pediu ao scanner, do mais antigo para o mais novo |
| `PickupFilter` | O filtro de coleta: o `PickupMode` e as entradas, uma por slot fantasma. Só conta o que o tier permite, e o resto fica guardado |
| `PickupMode` | Lista negra, a inicial, ou lista branca. A `key` é o nome usado nos saves |
| `PickupEntry` | Um slot do filtro: o ID de um item ou, no Elite e no Ultimate, de uma tag, salva com `#` |
| `PickupItem` | O que o filtro lê de um item no chão: o ID e as tags |
| `TabletApp` | Os apps do Tablet, um ativo por vez. A `key` é o nome no componente do item, e uma chave desconhecida vale Open |
| `RobotListing` | Um Cubit como a tela de seleção do Tablet o ordena: se segue o player, se está na mesma dimensão, se está carregado e a distância |
| `RobotListings` | A ordem e a busca da tela de seleção do Tablet |
| `TabletApps` | Os apps do Tablet que fazem sentido para o Cubit escolhido, a troca com Shift e a roda e a volta ao Open de um app que deixou de valer (perguntas 1006 e 1044). O Escort só entra com o card do Patrulheiro no Cubit escolhido (pergunta 1664) |
| `WorkArea` | A área de um Cubit: uma forma desenhada, do bloco marcado mais baixo ao mais alto, numa dimensão. Cabe no tier pelo retângulo em volta e pela altura, conta o bloco acima do topo, onde os itens ficam, dá os cantos para a ronda, sabe se toca outra área, a distância de um ponto até a base, o canto mais perto, para onde ir primeiro e quanto falta do caminho por dentro (perguntas 1062, 1625, 1636, 1707, 2240, 2244 e 2365) |
| `AreaShape` | A forma fechada no chão, só com ângulos retos. Lê as colunas uma vez, ao ser feita, e perguntar se um bloco está dentro não custa nada depois (perguntas 2243 e 2311) |
| `AreaPath` | Os pontos na ordem em que foram marcados: se a próxima linha é reta, se passa por cima do desenho e se o caminho fecha (perguntas 2243 e 2354) |
| `AreaSide` | Uma linha reta de blocos entre duas colunas |
| `AreaColumn` | Uma coluna de blocos da área, ou um ponto do contorno, sem altura |
| `AreaLoop` | Os cantos da forma numa ordem só, qualquer que seja o ponto em que o desenho começou. Um ponto no meio de um lado não é canto |
| `AreaWays` | O caminho entre dois lugares da forma sem sair dela, pelos cantos de dentro (pergunta 2365) |
| `AreaOutward` | O lado de fora, para onde o Patrulheiro olha nas paradas das bordas, também no canto de dentro de um L (perguntas 1626 e 1724) |
| `AreaBounds` | O retângulo em volta dos pontos, com a largura, a altura, a profundidade e o centro |
| `AreaDrawing` | O desenho pela metade: os pontos, se fechou, o que cada clique faz e por que é recusado (perguntas 2245, 2355 e 2356) |
| `AreaDrawingSpec` | O mínimo e o máximo de pontos e o alcance da mira (perguntas 2287 e 2311) |
| `AreaErase` | O primeiro dos dois cliques que apagam a área, com o tempo para confirmar (pergunta 2299) |
| `AreaCorner` | Um bloco marcado como ponto pelo app Área |
| `AreaCheck` | Se a área pode ser do Cubit, ou por quê não: sem modo área no tier, grande demais, em outra dimensão ou longe demais (perguntas 1050, 1054 e 1058) |
| `AreaMarking` | A marcação pela metade do app Área: o Cubit, a dimensão e o desenho, só na memória do servidor (perguntas 1054, 1060 e 2357) |
| `UnloadChest` e `UnloadChests` | Os baús de descarga de um Cubit, pelo bloco e pela dimensão, na ordem da marca. Um segundo clique desmarca, e o tier limita quantos (perguntas 1086 e 1088) |
| `MarkedDoor` e `DoorMarks` | As portas, os alçapões e os portões de cerca que o dono marcou com o app Portas, pelo bloco e pela dimensão, para todos os Cubits dele. Um segundo clique desmarca, e o config limita quantas (perguntas 1364 e 1369) |
| `UnloadCall` | Se o `Descarregar` do Tablet pode mandar o Cubit, ou por que não, e quando ele descarrega sozinho: só cheio, na área e com a chave ligada (perguntas 1087, 1089 e 1093) |
| `DeathPointLevel` | O que um tier faz com o local de morte: `locked`, `mark`, que só mostra a coordenada ao renascer, ou `guide`, que também guia até lá. A `key` é o nome usado no config (perguntas 1117 e 1129) |
| `RobotJob` | Os trabalhos que um card dá ao Cubit, um por card. A `key` é o nome usado no `allowed_jobs` do config (perguntas 1145 e 1163). `hibernatesWhenFull` diz se o inventário cheio, sem onde descarregar, o põe para dormir fora do Dock, o que o Patrulheiro nunca faz (pergunta 1639) |
| `JobSpec` | Os trabalhos que um tier aceita e o que ele faz com cada card, como o `FisherSpec` e o `FarmerSpec`. Sem nenhum, o slot do card fica bloqueado, como no Base (perguntas 1145, 1162 e 1185) |
| `FarmerSpec` | O Fazendeiro num tier: o custo e a pausa de cada ação, as plantas de colheita especial, a farinha de osso e a Fortuna, de 0 a 3 (perguntas 1221, 1226 e 1235) |
| `FisherSpec` | O Pescador num tier: os níveis de Isca e de Sorte do Mar, de 0 a 3, como os encantamentos do jogo, e o custo por peixe pescado (perguntas 1185 e 1190) |
| `HibernationReason` | Por que um Cubit com trabalho hiberna fora do Dock: o inventário cheio, da base, e o `NO_WATER` do Pescador sem água na área. A `key` é a do save (perguntas 1165, 1172 e 1188) |
| `AwayHibernation` | Quando um Cubit hiberna fora do Dock: com card, na área, cheio e sem onde descarregar, ou pelo motivo do trabalho. O inventário cheio vem antes. O `Retomar` pergunta de novo, então um motivo que não sumiu o põe para dormir outra vez (perguntas 253, 1165 e 1188). Quem guarda a área, como o Patrulheiro, não hiberna pelo inventário cheio (`keepsGuard`, pergunta 1639) |
| `WaterCell` e `OpenWater` | A água aberta do jogo, de `FishingHook.calculateOpenWater`: um volume de 5×4×5 em volta do ponto, em camadas de um tipo só, com água embaixo de ar. Só ela dá tesouro (pergunta 1183) |
| `FishingTimes` | O tempo de uma pesca, como no jogo: a espera sorteada, menos a Isca, e a mordida. Os limites vêm do config, e quem chama sorteia (pergunta 1184) |
| `FishingPace` | Quantos ticks da espera um tick conta: a chuva pode contar um a mais, e sem céu pode não contar, com as chances do jogo (perguntas 1184 e 1206) |
| `FishingSpot` e `FishingSpots` | O lugar na margem e o ponto da água a até o alcance do lançamento, e qual o Pescador escolhe: água aberta primeiro, depois o mais perto, sem um lugar que outro Pescador segura (perguntas 1188, 1189 e 1208) |
| `SpotReservations` | Os blocos que cada Cubit com trabalho segura, um por Cubit, só na memória do servidor: o lugar do Pescador ou a planta do Fazendeiro (perguntas 1189 e 1224) |
| `CropRipeness` | Se o Fazendeiro colhe uma planta: qualquer planta na idade máxima da `age`, também de outros mods, e as da tag que acrescenta, nunca as da tag que tira (perguntas 1218 e 1222) |
| `TillRules` | Se o Fazendeiro ara um bloco: o que a enxada do jogo transforma em terra arada, com ar em cima e água perto, nunca um bloco protegido (pergunta 1232) |
| `SeedChoice` | Que semente vai na terra arada vazia: a da planta vizinha e, sem ela, a que ele tem em maior quantidade (perguntas 1233 e 1236) |
| `WorkSettings` | As chaves da aba Trabalho de cada trabalho: por enquanto, o `Keep junk` do Pescador, ligado no começo. O Fazendeiro tem um slot de fertilizante no lugar de uma chave (perguntas 1191 e 1246) |
| `OrganizerMode` | O modo do Organizador, um por vez: organizar, no começo, ou abastecer (perguntas 1290 e 1301) |
| `OrganizerMark` e `OrganizerMarks` | Uma máquina ou um destino marcado com o app Marcar, pelo bloco, pela dimensão e pela face clicada, e as marcas em ordem, com o limite do tier, como os baús da Descarga (pergunta 1297) |
| `SupplyAmount` | A quantidade de um pedido de abastecer, de 1 a 64, com − e + de 1 em 1 e, com Shift, de 8 em 8, e o que ainda falta no destino (perguntas 1294 e 1300) |
| `ChestStock` e `StorageChoice` | O que cada baú guarda do item e se ele cabe, e a escolha do baú: o que tem mais do mesmo item ou, sem ele, o primeiro com espaço, na ordem das marcas (perguntas 208, 212 e 1291) |
| `SpeedChoice` | A velocidade que o player escolhe, em porcentagem da velocidade do tier, de 1 a 100, com 100% no começo. Ajusta um valor salvo aos degraus do config, anda um degrau com − e + e dá o fator, inteiro no perigo (perguntas 1259, 1266 e 1268) |
| `SpeedSteps` | O mínimo e o passo dos botões da velocidade, lidos do config por quem chama. O máximo é sempre 100% (pergunta 1267) |
| `JunkFilter` | O que o Pescador guarda: tudo com o `Keep junk` ligado, e, desligado, tudo menos o lixo da tag, que nunca inclui um item encantado (perguntas 1191 e 1207) |
| `DeathRecord` | Uma morte do player: o tempo do mundo, o bloco, a dimensão e a causa, no texto de morte do jogo (perguntas 1111 e 1114) |
| `DeathLog` | As mortes do player, da mais recente para a mais antiga, até o limite do config. Uma linha é achada pelo lugar e pelo tempo, para uma tela desatualizada nunca tirar outra morte (perguntas 1111 e 1115) |
| `GameClock` | O dia e a hora do mundo, como o jogo conta: o mundo começa às 06:00 do primeiro dia (pergunta 1116) |
| `DeathGuideCall` | Se o botão de guiar da aba Mortes pode mandar o Cubit, ou por que não, e quando o Cubit diz a coordenada ao renascer (perguntas 1111, 1117, 1118 e 1120) |
| `DeathGuideSpec`, `DeathGuideProgress` e `DeathGuideStep` | Os números da guia, o quanto ela avançou e o próximo passo: andar, esperar o dono, chegar ou desistir (perguntas 1121, 1123 e 1126) |
| `FollowCall` | Se o `Follow` do Tablet pode fazer o Cubit escolhido seguir o dono agora, ou por que não: já seguindo, sem carga ou em outra dimensão (perguntas 1073 a 1075) |
| `TabletWheel` | Para que lado Shift + roda troca o app do Tablet. Sem rolagem vertical, como no mouse do macOS com Shift, vale a rolagem para o lado, com o sinal trocado, como na hotbar do jogo |
| `IntroLink` | Quando o player viu o link da apresentação pela última vez, em ticks, com `NEVER` para nunca. O link só sai depois do temporizador, e um tempo anterior ao último link, como num outro mundo, o mostra de novo. Também diz se um endereço é seguro: só `https://`, com algo depois e sem espaços (perguntas 1674, 1677 e 1681) |
| `RobotName` | O nome que o Tablet dá ao Cubit, com as regras da etiqueta: corta os espaços das pontas, aceita até 50 caracteres e, vazio, tira o nome (perguntas 1015 a 1017). Sem nome, o padrão é `Cubit` e os 8 primeiros caracteres do UUID, e um nome vazio volta a ele (perguntas 1874 e 1878) |
| `PickupSettings` | As chaves da coleta, do ímã e da busca e o filtro, que o item guarda |
| `OreSignals` | Quais minérios avisam e qual deles o texto cita, a partir da lista dos raros |
| `ScannerSpec` | Números dos scanners de um tier: raios, intervalos, custos e vagas do filtro |
| `ScanRules` | Quando os scanners funcionam e o que conta como mob hostil |
| `ScanArea` | A esfera lida em volta do robô, com cada bloco do cubo que a contém numerado |
| `ScanSignal` | O que as antenas e o halo do corpo mostram dos scanners: mob, minério ou nada |
| `MarkLife` | Até quando uma caixa em volta do que um scanner achou continua desenhada |
| `MarkPulse` | A força do pulso suave das caixas dos scanners em cada momento |
| `FreshFinds` | O que um scanner já avisou, para só o que é novo avisar de novo |
| `ChatTopic` | O que o robô comenta e quantas frases tem para isso |
| `WeatherReading` | O que o robô lê do mundo para comentar: horário, céu, chuva, trovoada e frio |
| `ChatWatch` | O que mudou entre duas leituras e qual assunto isso dá |
| `ChatRules` | Quando o robô pode comentar algo |
| `BiomeWatch` | Quando o robô entra em outro grupo de biomas |
| `LeftBehindWatch` | Quando o robô reclama de o dono se afastar |
| `RespawnReturn` | Se o robô que segue volta com o dono que renasceu |
| `BatteryIndicator` | O que o indicador nas costas mostra: vazio, ou de 1 a 4 níveis, normal ou crítico |
| `LightLevel` | Nível de luz do jogo, de 0 a 15 |
| `LampPolicy` | Quando a lanterna acende e apaga, conforme a chave, o estado e a luz do ambiente |
| `RobotLight` | A luz final que o robô dá ao mundo: a tela e a lanterna |
| `LampPulse` | A força do halo das antenas em cada momento |
| `SolarOutput` | O que um painel, do Cubit ou do Dock, dá por tick, de dia e à noite, a taxa do momento, se dá à noite e o máximo do momento, que é o do dia quando ele não dá à noite (pergunta 1949) |
| `SolarPanel` | Quando o painel dá energia: ativo, com a chave ligada e com o sol chegando |
| `SolarPulse` | A força do brilho da célula em cada momento |
| `PowerSound` | Som tocado uma vez, ao ligar ou desligar |
| `ContinuousSound` | Som em loop enquanto a condição valer: esteira, curto elétrico e recarga |
| `PatrolSound` | Som do Patrulheiro Galático tocado uma vez, quando uma peça muda: o visor descendo e subindo, a lâmina acendendo e apagando e as asas abrindo e fechando |
| `BladeGlow` | Quando a Plasma Blade é pedida acesa: com o alvo a até a distância de acender, mesmo enquanto ele atira (`Timings`: a distância e o tempo depois). Contra o creeper, sem arma, não acende |
| `Linger` | Uma peça do Patrulheiro que continua ligada por um tempo depois do último momento em que foi pedida, sem desligar e religar se for pedida de novo a tempo: a lâmina acesa e as asas abertas |
| `PatrolGear` | As peças do Patrulheiro que mudam com o uso: o visor baixado, a lâmina acesa e as asas abertas |
| `RobotSoundRules` | Diz qual som cada situação toca, inclusive os de cada peça do Patrulheiro que mudou |
| `SlotRecharge` | Reserva de FE do slot de energia e sua passagem para a bateria |
| `EnergyTransfer` | Energia movida de uma bateria para outra num passo: o que a taxa, a origem e o espaço permitem, sem criar nem perder FE |
| `RechargeSplashSpec` | Os números da Recharge Splash, iguais em todos os tiers: a parte da vida e da bateria que ela dá, o raio a partir do impacto e os ticks em que ela age. Calcula a dose do tier, com a energia arredondada para baixo |
| `RechargeSplashDose` | O que uma Recharge Splash ainda tem a dar a um Cubit: partes iguais a cada tick, com o resto da energia no último, para a soma dar o total exato. `NONE` é o Cubit sem mistura |
| `RechargeSplashReception` | O que uma mistura faz com um Cubit: começa, se ele for de quem arremessou e estiver no raio; não dá nada e avisa, se ele ainda recebe a anterior; ou ignora |
| `WakeRecharge` | Religar um Cubit guardado com uma Power Bank na grade: só abaixo da carga de religar, só o que falta e só se a Power Bank tiver tudo |
| `PowerBankUpgrade` | A Power Bank encadeada leva a carga da anterior, e a receita não vale se a carga não couber |
| `EnergyStock` | Estoque de energia de um bloco do mod, como o Induction Dock, e as transferências dele, sem criar nem perder FE |
| `DockStatus` | O que a tela de um Dock mostra |
| `DockStay` | Vida recuperada por tick e quando o robô sai da base |
| `DockQueue` | Robôs aguardando um Dock ocupado, por ordem de chegada |
| `DockTrip` | Um robô a caminho do Dock: o quanto chegou perto, há quanto tempo não avança e o que faz em cada tick |
| `DockGoal` | Se o `Ir para o Dock` do Tablet pode mandar o Cubit ou por que não: desligado, hibernando ou sem Dock ao alcance (perguntas 1020 e 1041) |
| `PreferredDock` | O Dock marcado com o app Dock do Tablet, pelo bloco e pela dimensão. Clicar no mesmo Dock tira a marca (perguntas 1008 e 1045) |
| `AutoReturn` | Quando o Cubit volta sozinho a um Dock: com a chave ligada, ativo, seguindo o dono e com a bateria do retorno do tier ou menos, a não ser que já esteja a caminho (perguntas 1065 a 1068). Em guarda (`onGuard`), o Patrulheiro espera o fim do combate e um raio de procura sem hostil, sem conferir o dono nem investigar (perguntas 1689 e 1763) |
| `DockRemoval` | Quem pode quebrar um Dock |
| `DockStorageRules` | O inventário do Dock: o que ele guarda, quais slots o tier abre, quando e qual Power Bank ele carrega, e o que a automação tira (perguntas 1361 e 1374) |
| `StoredKind` | O que um item é para o inventário do Dock e do Network Server: Toolbox, Power Bank, Cubit, outro item do mod ou um item de fora |
| `DockSpec` e `DockCatalog` | Os números do Dock em cada tier: o estoque, a recarga do Cubit e da Power Bank guardada, os slots, em linhas inteiras de 9, e a geração do painel, de dia e à noite. O config os serve (perguntas 1374, 1381, 1385 e 1944) |
| `SkySun` | O que um painel vê, o do Dock ou o do Solar Relay: dia, céu aberto, chuva, um mundo com sol e o espaço do painel, que no Dock é o 3º bloco. Só gera com tudo a favor, e sem sol é um Dock normal. Um Dock de antes, sem espaço para o 3º bloco, fica sem o painel, e só um mundo sem sol é um motivo mais duradouro. No tier que gera à noite, a noite não para o painel, que fica em `MOONLIGHT`, e a chuva o para como de dia (perguntas 1384, 1897 e 1943) |
| `FoldingPanel` | Um painel solar que se dobra, o do Dock ou o do Solar Relay: abre depois de um tempo seguido com sol e volta depois do mesmo tempo sem sol, também à noite, menos no tier que gera à noite (pergunta 1943); o movimento dos braços vai até o fim, qualquer que seja o céu; só o painel montado por completo gera; e só o destino do movimento é salvo (`Timings`: a espera e o movimento) (perguntas 1856, 1859, 1862 e 1864) |
| `PanelState` | Onde um painel está: guardado, montando, montado ou guardando, para a aba Solar e a luz das portas (perguntas 1863 e 1865) |
| `DockPanelSound` | O som de cada momento do painel: a montagem quando os braços começam a sair, a trava quando as metades se encontram no centro e a desmontagem quando os braços começam a voltar (perguntas 1861 e 1921) |
| `DockDoorLight` | A luz na borda das folhas das portas: vermelha com o painel guardado, verde com ele montado e amarela, piscando, durante o movimento (perguntas 1853 e 1863) |
| `BlockName` | O nome de um bloco com dono, como o Dock: o dado pelo dono, com as regras do nome do Cubit, ou o padrão, o começo do tipo dele (`Dock`, `Server` ou `Relay`) e os 8 primeiros dígitos do UUID, para ele nunca ficar sem nome (perguntas 1867 a 1869) |
| `ServerSpec` e `ServerCatalog` | Os números do Network Server em cada tier: a bateria, os canais, a entrada por cabo e redstone, os slots, em linhas inteiras da tela, e o que o Solar Relay dele gera de dia e à noite |
| `UpsReserve` | A parte da bateria que é só do chunk, o Nobreak, de 0% a 50% em passos de 5, com o que sobra para o resto e quanto tempo ela segura. Um número salvo ou editado vai ao passo de baixo, dentro do limite (perguntas 2209, 2212 e 2221) |
| `UpsWatch` | O aviso do Nobreak: uma vez ao descer para a reserva, e de novo só depois de sair dela |
| `ChunkCall` e `ChunkState` | Se um bloco segura o chunk e por que não, com o motivo em que o player menos pode agir primeiro: o config, a chave, o dono offline, a energia e os limites. Quem zerou só volta com a carga da retomada (perguntas 2210 a 2215 e 2372) |
| `ChunkLimits` | Quantos chunks o mod pode segurar, de um player e no mundo, com 0 para sem limite (pergunta 2215) |
| `ServerPower` | O que a bateria fez num tick: ligou, caiu ou nada, para a partida e a queda |
| `ServerFront` | A frente do Servidor: as colunas de 10 traços, com ao menos um aceso acima de zero, e o estado de cada lâmpada |
| `BigSlot` | Quantos itens um slot do Servidor guarda e dá: até o limite dele, de 1 a 999, 1 do que não empilha, e nunca mais que uma pilha normal por vez (perguntas 2263 e 2368) |
| `SunTrack` | Para onde o painel do Solar Relay aponta, de −1, no leste, a 1, no oeste, e 0 ao meio-dia e com a lua (pergunta 2385) |
| `RobotClick` | O que o clique direito faz no robô: ignorar, recolher, subir o tier, pintar, escolher o minério, abrir ou ligar |
| `InventoryFit` | Separa os itens que cabem nos slots do config dos que sobram |
| `InventoryGrid` | Como o inventário aparece na tela: sempre 3 fileiras, com 9, 12, 15 e 18 colunas do Base ao Ultimate, a coluna e a fileira de cada slot e as colunas a mais, que alargam a tela (pergunta 1101) |
| `Ownership` | Diz quem é dono de um robô ou de um Dock, e se os dois têm o mesmo dono |
| `OwnerProtection` | Diz se um dano é do dono e, por isso, não fere o robô |
| `VoidRescueTarget` | Para onde vai o robô danificado que caiu no vazio |
| `FollowingOutcome` | Resultado da política: estado do robô, quem segue e quem desligar |
| `RobotScreenStatus` | O estado no topo da tela do Cubit: bateria crítica, seguindo, ativo ou hibernando |
| `EnergyFormat` | Como as telas escrevem a energia: quantidades curtas, como `214k`, e taxas inteiras, como `2,400` |
| `EnergyHalves` | Divide uma quantidade de energia em duas metades de 16 bits e as junta de volta |
| `SolarStatus` | O que a tela diz do painel solar: carregando, coberto, noite, sem sol, bateria cheia ou desligado |
| `EnergyUseCause` | Por que o robô gastou energia no tick, na ordem que a tela lista |
| `RechargeSource` | De onde a bateria recebeu energia no último tick: Dock, slot, painel ou nada |
| `HealTime` | Quanto tempo a base leva para encher a vida, em minutos |
| `InputMeter` | A média do que entrou num bloco do mod por tick, no último segundo |
| `DeskSpec` | Os números da Engineering Desk num tier: bateria, ticks de um craft, slots de cada inventário e maior pedido |
| `DeskCatalog` | Interface que dá o `DeskSpec` de um tier, servida pelo config |
| `DeskOrder` | O pedido da mesa: quantos itens foram pedidos e quantos estão prontos, com o sinal do comparador |
| `CascadeRecipe` e `CascadeBook` | Uma receita como a cascata a vê, e a receita de cada item, a primeira na ordem do catálogo (perguntas 2009 e 2032) |
| `CascadePlanner`, `CascadePlan` e `CascadeTask` | O plano de um item: as tarefas em série, com as peças antes, os materiais de base e as peças aproveitadas. Um ciclo não tem plano (perguntas 2006, 2008, 2012 e 2034) |
| `CascadeRun` e `CascadeTaskStatus` | O plano em andamento, uma tarefa por vez, que nunca volta, com o status de cada uma e o sinal do comparador |
| `CascadeCheck` e `CascadeStart` | O que a mesa encontrou para iniciar um plano, e se ele pode começar ou o primeiro motivo (perguntas 2009, 2015 e 2028) |
| `CascadeTable` e `CascadeLine` | A tabela de um plano, igual antes e depois do início: a ordem, o item, o que a tarefa consome, de quais depende e o progresso. Em andamento, o resto sai das receitas (pergunta 2024) |
| `CascadeAmount` | Quantos itens um plano pede, de 0, como um item escolhido começa, ao maior pedido do tier (perguntas 2011 e 2053) |
| `CascadeNeed` | O que um plano usa de um item: um material de base ou uma peça pronta (perguntas 2006 e 2052) |
| `CascadeCost` | A energia e o tempo de um plano, que a tela mostra e não exige (pergunta 2013) |
| `MaterialLevel` e `CascadeChoice` | Quanto a entrada tem de um material, e o item escolhido numa marca com vários (perguntas 2006 e 2033) |
| `DeskOrderGate` | O que um pedido precisa para sair de zero: a receita liberada pela progressão e os materiais de um item. Só o começo é travado (perguntas 1984, 1985 e 1991) |
| `DeskProgress` | Os ticks do craft em andamento, mantidos na pausa |
| `DeskCheck` | O que a mesa encontrou no tick: pedido, sinal de redstone, materiais, receita aceita, energia e espaço na saída |
| `DeskStatus` | O que a mesa está fazendo: parada, criando ou pausada, com o motivo |
| `DeskStock` | A energia guardada na mesa, recebida pela entrada e paga por craft |
| `DeskLook` | O que a mesa mostra aos clientes: parada, criando ou em pausa, com a luz só criando |
| `DeskLid` | Se a tampa do laptop fica aberta: com alguém na tela ou com um pedido ativo |
| `DrawerPace` | Quando uma gaveta da mesa pode abrir de novo, a partir da hora do jogo |
| `DeskCategory` | As categorias do catálogo da mesa, na ordem dos ícones, com Ferramentas como padrão |
| `CatalogSearch` | A busca do catálogo: o nome que o player vê, sem maiúsculas e acentos, em todas as categorias |
| `JobTalkTopic` | O que um Cubit com card fala: uma das três frases ao receber o card e as frases de cada momento do trabalho |
| `JobTalkRules` | Quando ele fala: a chance, quem ouve, a chave das falas e a expressão calma, e se o card é novo |
| `PatrollerSpec` | O que um tier faz com o card do Patrulheiro Galático: a pistola, a lentidão, a lâmina, a armadura extra, o raio de procura, o salto, com o vão que atravessa planando (`gap`), os pontos da rota, o lado da área, o dobro do dos outros cards, o alcance do golpe da lâmina (`bladeReach`) e a bateria do retorno automático (`returnCharge`), de 5% no Elite e 3% no Ultimate (perguntas 1642, 1648 e 1689) |
| `PatrolSettings` | Os números do Patrulheiro que valem em todo tier: a chance e o tempo da lentidão, a chance da cabeça, a perseguição, a distância da troca de arma e o passo da ronda (`Round`: a velocidade e os limites de cada parada e dos blocos entre duas paradas, com o sorteio feito por quem chama e mantido nos limites) |
| `RoutePoint` | Um ponto da rota: o topo de um bloco sólido e a dimensão dele |
| `PatrolRoute` | Os pontos da rota, na ordem em que foram marcados, percorridos em laço, sem ponto repetido e até o limite do tier, e o ponto mais perto de um lugar, de onde a ronda volta depois de um combate (pergunta 1625) |
| `RouteMarking` | O que um clique do app Marcar faz na rota: desmarca um ponto marcado, ou marca o topo de um bloco sólido dentro da área, com o motivo da recusa; e o Shift, que tira o ponto clicado ou o último |
| `PatrolProgress` | Para qual ponto da rota o Cubit vai, em laço |
| `PatrolSiege` | O cerco da equipe num alvo: o lado de cada Patrulheiro, em ângulos iguais a partir do primeiro, que mantém o seu, e a distância de cada arma, a da lâmina perto e a da pistola entre a troca de arma e o alcance |
| `PatrolSpread` | A ronda de vários Patrulheiros na mesma rota ou nas mesmas bordas: quando dois vão ao mesmo ponto, o que chegou depois segue para o ponto do laço mais longe dos colegas, e o empate se decide pelo id |
| `FollowStuck` | Quem segue e fica sem caminho até o dono: sem avançar por um tempo, aparece perto, uma vez até ter caminho de novo (pergunta 2092) |
| `XpRate` | Quanto fluido vale 1 ponto de XP (pergunta 1817) |
| `XpTank` | O tanque de XP, em mB, do Cubit e do Dock. Só move pontos inteiros e diz quantos moveu. Com a capacidade reduzida, guarda o que passa e não aceita mais (pergunta 1937) |
| `XpSpec` e `DockXpSpec` | O tanque do Cubit e, do Dock, o tanque e a passagem, em cada tier (perguntas 1826, 1833 e 1835) |
| `XpMode` e `XpSettings` | O modo do Cubit, Ignorar, Armazenar ou Voltar ao Dock, e a chave do excesso (pergunta 1827) |
| `XpState` | O que o Cubit guarda do XP no mundo e no item (pergunta 1838) |
| `XpIntake` | O que o Cubit faz com o XP do trabalho e com as bolinhas, por modo: guarda, deixa, derruba ou apaga. O XP que o player envia nunca passa por aqui (perguntas 1819, 2064, 2065 e 2080) |
| `PlayerXp` | O XP do player em pontos inteiros, pelas contas do jogo: o que falta para subir um nível e o que se entrega para descer um (pergunta 2076) |
| `XpFlow` | O ritmo do XP no tier do Dock, em pontos inteiros: a passagem do Cubit e a conversão em energia (perguntas 1835 e 1939) |
| `XpReturn` | Quando o Cubit cheio vai ao Dock (pergunta 1934) |
| `XpConversion` | Quanta energia vale 1 ponto de XP, sem caminho de volta, e quantos pontos inteiros têm espaço para a energia deles (perguntas 1942 e 2111) |
| `XpEnergyTotal` | A energia que o XP de um Dock gerou, só para ler, que para no maior número (pergunta 2109) |
| `DockXpMode` | O que o Dock faz com o XP: apaga tudo, guarda e apaga o excesso, guarda e converte o excesso ou converte tudo (perguntas 1831 e 2100) |
| `DockXpRules` | O XP que o Dock recebe, sempre inteiro, guardado, convertido ou apagado, e o guardado, que só o Converter tudo gasta (perguntas 1831, 2061 e 2101) |
| `DockXpNotice` | O aviso do tanque do Dock cheio, que só volta abaixo de 90% (pergunta 2105) |
| `XpUnloadCall` | Se o Descarregar XP do Tablet manda o Cubit ao Dock, ou por que não (perguntas 2079 e 2107) |
| `XpNotice` | O aviso do tanque do Cubit cheio, uma vez por enchida (pergunta 1840) |
| `WingHop` | O pulo de asa do Patrulheiro: quando vale pular (o caminho do jogo não chega ou passa do fator da linha reta), a altura do voo curto sobre o que está no caminho, dentro do salto do tier, e o movimento de cada tick, que sobe, avança e plana até o pouso (perguntas 1647, 1648, 1651 e 1738) |
| `PatrolWatch` | O que o Patrulheiro faz como vigia: a ronda, o alerta ao avistar um hostil, pulado quando ele já fere alguém, o combate, a conferida no dono depois do último abate e a investigação do último lugar onde viu um hostil que sumiu, com o visor, que baixa no alerta e só sobe depois da calma ou da investigação (`Timings`: os tempos, a distância do dono e o tempo até o alvo sumir de vista) |
| `PatrolStops` | As paradas da ronda: os blocos andados desde a última, o trecho sorteado até a próxima, a parada em curso e para onde a cabeça olha, duas vezes para cada lado e depois à frente |
| `PatrolTraits` | O que o Patrulheiro sabe de um mob, lido dos sinais do jogo, sem lista fixa: se o ataca (todo `Enemy`, `MONSTER` e `NeutralMob`, e quem fere um protegido) e se o protege (players, robôs, aldeões e animais calmos). A tag de ignorar vence, e os protegidos nunca entram na de atacar |
| `WeaponChoice` | A arma que o Patrulheiro usa no alvo: a pistola de longe e no ar, a lâmina de perto e nenhuma no creeper que ele não pode alvejar. Ferido, com cautela, ele usa a pistola mesmo de perto, e a lâmina só sem poder atirar (pergunta 1686). Contra um alvo que o globo não fere, como o enderman, que desvia, e o breeze, que o rebate, só a lâmina (perguntas 1695 e 1696) |
| `PatrolTargets` | O hostil que a equipe de Patrulheiros ataca, todos juntos: primeiro o mais perto de um Patrulheiro ferido, depois o que fere alguém, depois o mais perto de um protegido. A equipe mantém o alvo até ele cair ou sair do alcance, salvo se surgir um de tipo mais urgente |
| `PatrolTeam` | A equipe dos Patrulheiros de um dono: quem luta junto (mesma dimensão, com áreas que se tocam ou dentro do chamado), o alcance do chamado, o raio do socorro e a vida abaixo da qual um Patrulheiro está ferido |
| `PatrolRules` | O modo sempre Agressivo, os modos que o card trava na aba Defense, o limite da perseguição, quando ele salta e quando recua do creeper. Também guarda o escudo contra o creeper, o empurrão da lâmina e o cone de 20° de um tiro que vai para alguém (perguntas 1642, 1731 e 1735) |

## Regras e porquês

- **Falas pelo que muda:** o robô comenta quando algo muda, para cada fala ter um motivo, e o sorteio só escolhe a frase. O que ele não pôde comentar se perde, para não anunciar uma tempestade que já passou (perguntas 572 a 583).
- **Redstone, sempre que calmo:** o Cubit ama redstone, então fala quando o dono a minera, sem chance. Só com a expressão calma, para nunca cobrir o aviso do scanner (perguntas 619 e 620).
- **Animal abatido, às vezes:** a chance evita uma fala a cada animal numa fazenda, e um animal de estimação nunca vira piada (pergunta 587).
- **Bioma por grupo:** só uma mudança de grupo conta, e um bioma sem grupo, como um rio, não muda nada, para o robô não repetir o comentário (pergunta 586).
- **Reclamar uma vez:** ao se afastar, o dono só ouve de novo depois de o robô chegar perto, para não repetir a cada passo. Um salto além do teleporte não conta, porque não é o dono correndo (pergunta 585).
- **Apresentação uma vez:** o Cubit fica apresentado antes de dizer qualquer coisa, então o que ficou quieto, pelo temporizador do link ou pela chave `Chatter` desligada, nunca fala depois (perguntas 1678 e 1753).
- **Falas só com a expressão calma:** qualquer outra expressão indica algo mais importante, como um mob ou um dano.
- **Sorteio fora do domínio:** o tempo da pesca e as chances da chuva e do céu são sorteados por quem chama, e o domínio só recebe os números. Assim ele continua sem dependências ocultas e testável sem relógio nem sorte (pergunta 1184).
- **Recharge Splash aos poucos:** a dose é dividida a cada tick, e o último tick leva o que sobra, porque a energia é inteira e a soma precisa dar o total exato (regra 13 e pergunta 1415). O que não cabe na bateria cheia se perde, porque a mistura é uma fonte, como o painel, e não uma transferência entre duas baterias.
- **Hibernação fora do Dock como estado próprio:** a do Dock sai sozinha com a carga cheia, e a de fora só sai pelo `Retomar`, pelo `Follow` ou pelo `Ir para o Dock`, então misturar as duas obrigaria toda regra do Dock a conferir onde o Cubit está (pergunta 1164).
- **Histórico próprio:** o jogo guarda só a última morte, sem a hora e sem a causa, então o mod guarda o seu, com um limite, para o save não crescer sem fim (perguntas 1114 e 1115).
- **Guia à frente, sem sair do alcance:** o Cubit espera o dono quando passa do limite à frente dele, e por isso nunca anda por chunks que só ele carregaria (pergunta 1126 e regra 22). Esperar o dono não conta como estar preso (pergunta 1123).
- **Volta com o dono:** um robô longe, ou em outra dimensão, ficaria para trás ao descarregar o chunk. Perto, ele anda como sempre, e a caminho do Dock continua indo, porque o dono o chamou (pergunta 567).
- **Só o que é novo avisa:** o mesmo mob ou bloco não avisa de novo enquanto continuar no raio, porque o intervalo mínimo sozinho repetiria o mesmo minério a cada varredura. O que sai do raio é esquecido (pergunta 561).
- **Perigo primeiro nas cores:** o vermelho de um mob vence o azul de um minério, como o alerta vence a surpresa. O sinal não depende da expressão, para um dano não apagar o vermelho com o mob ainda no raio (perguntas 638 e 639).
- **A mesa mostra menos do que sabe:** os motivos da pausa ficam no servidor, e o cliente só precisa saber se a mesa está parada, criando ou esperando, o que cabe num estado de bloco e dá a luz sem pacote próprio (perguntas 1508 e 1513).
- **Gaveta com intervalo:** a automação move itens a cada tick, e sem o intervalo a gaveta piscaria. Um relógio que voltou, por um comando de tempo, não trava a gaveta (pergunta 1511).
- **Categoria padrão:** uma receita sem categoria, ou com uma desconhecida, vai para Ferramentas, e nunca some do catálogo por um erro de digitação num pack (perguntas 1521 e 1522).
- **Falas das profissões por perto:** um Cubit trabalhando fica longe do dono, então as falas do trabalho vão para o dono a até uma distância, mesmo sem segui-lo, com chance e intervalo, para uma fazenda grande não virar spam. O sorteio fica com quem chama (perguntas 1585 e 1586).
- **Patrulheiro sempre Agressivo:** atacar primeiro é a essência do card, então o modo escolhido fica guardado e volta quando o card sai (pergunta 1566).
- **Lâmina sem bateria:** só a pistola e o salto gastam energia, para ele nunca ficar sem defesa. Sem carga, ele não chega perto do creeper e fica entre ele e os protegidos (perguntas 1571 e 1595).
- **Ronda de vigia:** o passo lento e as paradas valem só na ronda. Na perseguição, ele corre na velocidade normal, e qualquer parada, num ponto, num canto ou no caminho, zera a contagem de blocos, para as paradas não se amontoarem (perguntas 1614 a 1618).
- **Desmarcar vem antes de recusar:** um ponto marcado sai mesmo fora da área nova, para o player limpar uma rota antiga sem marcar a área de novo (pergunta 1611).
- **Rota só na área:** os pontos fora da área ou de outra dimensão não valem, mas ficam guardados no NBT, e voltam a valer quando a área os inclui de novo (perguntas 1599 e 1602).
- **Forma calculada uma vez:** a área guarda as colunas dela num mapa de bits, com até 256 blocos de lado e 256 pontos, para um save quebrado não tomar a memória. Nenhuma conta de polígono roda por tick (regra 21 e detalhe 8 da Network).
- **Mesma forma, mesma área:** os cantos têm uma ordem só, então dois Patrulheiros com o mesmo desenho, começado em pontos diferentes, dividem as mesmas bordas (perguntas 1646 e 1708).
- **Progresso pelo caminho:** num L, contornar o corte afasta o Cubit do alvo em linha reta. O progresso conta o caminho que falta por dentro, para ele não desistir no meio (pergunta 2365).
- **Esfera, não cubo:** o canto de um cubo fica a cerca de 1,7 vez o raio, então só conta o que está a até o raio em linha reta. Os dois scanners usam a mesma regra (perguntas 604 e 606).
- **Varredura por número:** a área numera os blocos, então uma varredura em fatias só guarda um número entre os ticks, sem alocar nada (regra 21).
- **Bloco pela subtag:** um bloco está em `c:ores` e nas subtags, e só elas dizem o que ele é. `c:ores` só vale para quem não tem subtag, então um filtro nele não pega todo minério (perguntas 546 e 566).
- **Raro vem antes do filtro:** o texto cita o raro mais raro e, só sem raros, um minério do filtro (pergunta 562).
- **Filtro cheio troca o mais antigo:** assim, um clique sempre funciona, mesmo com só uma vaga (pergunta 548).
- **Mesma cor não gasta:** pintar da cor atual não faz nada, para não desperdiçar o corante (pergunta 509).
- **Só o ativo reage:** desligado, a tela está apagada, e hibernando ela sempre mostra carregando (perguntas 57 e 510).
- **Energia inteira:** evita erro de arredondamento, e uma transferência nunca cria nem perde FE.
- **Operações informam o que moveu:** `recharge` devolve o que foi aceito, e `consume`, o que foi consumido. Assim, quem chama sabe o que sobrou e não perde energia.
- **Expressões:** o estado vem primeiro, porque tela apagada e carregando não dependem do resto. Depois vêm vida baixa e dano, os avisos e o sono, e a feliz cede a eles (perguntas 368 e 379). O bipe não toca ao sair de uma tela apagada ou carregando, porque ligar e ser colocado têm som próprio e carregar o mundo não é uma troca (pergunta 369).
- **Pose pela expressão:** o cliente já recebe a expressão, e tela apagada e carregando só existem com o robô desligado ou na base, então a pose não precisa de um dado novo. Empurrado, ele continua na pose de desligado (pergunta 394).
- **Braços junto do dano:** ficam erguidos por um tempo menor que o da expressão triste ou de olhos em X, sem parar o balanço do corpo (perguntas 403 e 445).
- **Olhar para o dono:** um pulo ou uma queda rápida movem os olhos por um tempo, e estar em outro nível os mantém enquanto durar. O limite da queda fica acima da velocidade de pousar de um pulo, para o pulo não terminar com os olhos para baixo. O movimento do próprio robô vence (perguntas 404, 406 e 407).
- **Inclinação só por nível:** o corpo inclina de leve com o dono em outro nível, mas não num pulo, que move só os olhos, para ele não balançar a cada salto (perguntas 410 e 411).
- **Números das animações em constantes:** são só visuais, como os da piscada (pergunta 398).
- **Olhar sem tremer:** a direção fica um tempo depois do movimento, porque um degrau dura poucos ticks (perguntas 375 e 376).
- **Sons:** entrar na base não toca o som de desligar, porque a recarga já toca, e a recarga só vale com a carga subindo de fato, para não tocar com a bateria cheia nem com o Dock sem energia (perguntas 358 e 359).
- **Sons do Patrulheiro:** cada peça que muda toca o seu som, na ordem visor, lâmina e asas, sem caso especial: ao tirar o card, tudo se recolhe e os sons de apagar tocam, como em qualquer troca (perguntas 1792, 1797 e 1799). O disparo, o golpe, o acerto, o estouro e o impulso não dependem de um estado e tocam onde a ação acontece.
- **Barra sem falso zero:** `level` só devolve 0 com a bateria vazia. Com qualquer carga, devolve ao menos 1, para o player não achar que o robô ativo está sem energia (pergunta 269).
- **Indicador crítico em todos os níveis:** o limite crítico vem do config e pode cobrir mais de um nível, então o vermelho não pode reduzir o preenchimento. O vazio vence o crítico, porque só vale com 0 FE (perguntas 415 e 416).
- **Brilho junto da tela:** a tela e o indicador brilham pela mesma regra, `RobotExpression.glows`, para nunca discordarem (pergunta 417).
- **Aviso só do ativo:** um robô desligado ou na base não fala (perguntas 429 e 443). Quem chama garante que é o robô que segue (pergunta 430).
- **Intervalo corre sempre:** a espera continua mesmo sem a condição, para um robô que oscila em volta do limite não repetir o aviso antes da hora.
- **Comparação de porcentagem sem divisão:** `hasAtLeast` multiplica os dois lados, para ser exata com os valores de `tiers.md`.

- **Desligar pela tela, só o ativo:** no Dock o robô hiberna, e desligado ele já está desligado, então só o robô ativo pode ser desligado pela tela (pergunta 857). Ele volta a ligar pelo clique, como qualquer robô desligado com carga.
- **Tela com a altura do Base:** o inventário dos tiers altos rola, em vez de a janela crescer, porque no Ultimate ela passaria da altura da tela do jogo (pergunta 838).
- **Choque só com quem o usa:** o choque e os três modos entram juntos, porque o Pacífico sozinho nunca dispara o choque (pergunta 822).
- **Pacífico sempre liberado:** o `ShockSpec` acrescenta o Pacífico a qualquer lista de modos, para o dono sempre poder parar os ataques, mesmo com um config que esqueça dele (pergunta 834).
- **Focus sem alvo defende:** sem alvo, o Focus age como o Contra-atacar, para o dono nunca ficar sem defesa (pergunta 848). Com alvo, só o alvo leva o choque, mesmo um neutro calmo, porque o dono o feriu (pergunta 845).
- **Nunca atacados:** o dono, outros players, outros robôs e mobs pacíficos, em qualquer modo. Um mob neutro só vira alvo quando fica agressivo contra o dono (perguntas 13 e 68).
- **Tempo sem relógio:** a `RetaliationMemory`, a `FetchMemory`, as `ShockRules` e as `FetchRules` recebem o tick atual de quem chama, como o resto do domínio.
- **Coleta e ímã separados:** a coleta por contato vale em todo tier e não gasta. O ímã precisa da coleta ligada, porque puxar sem pegar só amontoaria itens, e só puxa com carga para o custo inteiro de um item (perguntas 876, 877 e 879).
- **Tochas sem desperdício:** um lugar é perigoso pela luz de bloco, porque a luz do céu não protege à noite, e ao ar livre ele só conta quando a lanterna acenderia. O `TorchPlanner` simula a luz de cada lugar possível, que cai 1 por bloco e não atravessa parede, e escolhe o que deixa mais blocos seguros. Sem bloco a deixar seguro, nenhuma tocha é posta (perguntas 971 a 979).
- **O Cubit antes das Power Banks:** no Dock, as Power Banks do inventário só carregam sem Cubit na base ou com a bateria dele cheia, uma por vez, para nenhuma encher antes do robô nem dividir a recarga (perguntas 949 e 952).
- **Busca em volta do dono:** o raio da busca conta do dono, não do robô, porque o item jogado com Q cai à frente dele, fora do alcance do ímã (perguntas 927 e 936). A busca precisa da coleta ligada, como o ímã, não tem custo além do andar e fica abaixo do Dock, do fogo, dos blocos que ferem e do choque (perguntas 937 a 939).
- **Filtro sem apagar:** as entradas acima do limite do tier e as tags num tier sem tags ficam guardadas sem contar, para um config maior trazê-las de volta (pergunta 892).
- **Power Bank é uma `Battery`:** a carga dela usa a mesma bateria imutável do Cubit, e toda passagem de energia, no slot, no Dock ou na grade, usa o `EnergyTransfer`, para nunca criar nem perder FE (regra 13 e pergunta 907).
- **Religar arredonda para cima:** a carga de religar é arredondada para cima, para o Cubit nunca ficar 1 FE abaixo dela depois da receita (pergunta 181).
- **Comparação pelo ID:** o filtro compara só o ID do item, nunca os componentes, para uma espada encantada valer por todas (pergunta 883).
- **Números por dado:** as regras recebem os números de um `TierSpec`, sem constantes no código (regra 39 do `AGENTS.md`).
- **Upgrade sem pular etapas:** um Module só leva ao tier logo depois do atual. O que pula uma etapa ou chega a um tier igual ou menor não é consumido, e o motivo vira o texto da recusa (perguntas 226, 802 e 808). A ordem dos tiers é a do `enum`, então um tier novo entra no lugar certo da lista.
- **Estado guardado se ajusta ao config:** o config pode mudar enquanto o robô está no item, então `batteryFor` e `healthFor` limitam os valores ao `TierSpec` atual. A vida nunca fica abaixo de 1, porque um robô colocado sem vida morreria na hora.

- **Só o ativo se move:** inativo ou em hibernação, o robô para e não teleporta. Em outra dimensão também para, porque o teleporte só vale na mesma dimensão.
- **Sair da lava vem antes de buscar água:** é o que mais fere. A rota nunca entra em lava, então a saída é direta, até o bloco seguro mais próximo (pergunta 139).
- **Bloco que fere:** só o ativo sai de cima, porque é o único que se move, e fogo e lava vêm antes, porque ferem mais (perguntas 284 e 285).
- **Em chamas, a reação vem antes de seguir:** só o robô ativo reage, porque é o único que se move. Parado, ele continua queimando (pergunta 117).
- **Distância inválida:** o robô para. É o estado mais restritivo, e o jogo não cai por isso (regra 17 do `AGENTS.md`).
- **Colocar e sair da base são a mesma regra:** o robô segue se nenhum outro estiver seguindo. Se houver outro, fica desligado, porque o clique direito só liga um robô desligado, e assim o player consegue fazê-lo seguir (perguntas 72, 75 e 102).
- **Sem bateria, o vínculo continua:** o robô para e, ao religar, volta a seguir. Ligar outro robô o desliga, e ele deixa de religar sozinho (pergunta 101).
- **Quem guarda o vínculo é quem chama:** a política recebe quem segue e devolve o resultado, sem ler os dados do player.
- **Ativo sem o lugar de seguir:** um robô ativo que descobre que não é o que segue se desliga, porque só um robô desligado volta a seguir com o clique. É o caso de quem estava em chunk descarregado na troca e do robô que perdeu o vínculo pelo `/cubit reset` (pergunta 326).
- **Morte e conserto zeram a bateria:** ao morrer, o robô fica sem vida e sem energia. O conserto devolve parte da vida, e o Induction Dock recupera o resto (perguntas 34 e 129).
- **Sem dono, a resposta é não:** dois `Optional` vazios são iguais em Java, então a regra exige um dono presente, para um robô sem dono não combinar com um Dock sem dono (regra 20 do `AGENTS.md`).
- **Dano do dono:** só conta o que o jogo atribui a ele. O fogo de um isqueiro não tem autor, então fere (pergunta 119).
- **Resgate no vazio:** a ordem é a posição segura, o dono online e o spawn do mundo. Sem destino possível, o item espera, porque nunca é destruído nem força um chunk a carregar (perguntas 147, 172 e 174).
- **Consumo por deslocamento:** quem chama informa se o robô andou, e o domínio só gasta nesse caso (pergunta 257).
- **Consumo por situação:** peso, água e subida só somam ao andar. A condição negativa soma mesmo parado, e só uma vez, ainda que haja várias. A lanterna acesa também soma parado (pergunta 469). O robô inativo não gasta (perguntas 271 a 278).
- **Dois limites na lanterna:** ela acende abaixo de um e apaga a partir do outro, para não piscar perto do limite (pergunta 470). `fitting` existe porque o config pode trazer os dois invertidos, e o construtor rejeita isso.
- **Dia pelo relógio:** o painel olha o horário do mundo, e não a luz do céu, porque o clima não tem efeito (pergunta 53).
- **Só a luz espera:** a chave e o estado valem na hora, e só a leitura do mundo roda em intervalos, para a lanterna nunca ficar acesa num robô desligado (pergunta 474).
- **Luz junto da tela:** `RobotLight` dá zero com a tela apagada, pela mesma regra do brilho, `RobotExpression.glows` (perguntas 378 e 465).
- **Lanterna na hibernação:** apagada e sem consumo, porque o robô está imóvel e carregando. A tela continua dando a claridade (perguntas 465 e 466).
- **Subida em FE inteiro:** o extra é arredondado, porque energia é inteira (regra 13). Descer não devolve nada, porque criaria FE.
- **Preso a caminho do Dock:** um movimento pequeno não conta como avanço, para um robô contra uma parede desistir. Preso com o Dock livre, ele desiste, e nunca se teleporta nem entra através de um bloco (pergunta 2091). Preso com o Dock ocupado, ele aguarda onde está. Perto do Dock, o tempo não conta, porque ele pode estar esperando a vez (perguntas 84 e 301).
- **Ordem do clique:** o Shift sempre recolhe, inclusive com corante ou Module na mão. O Module e o corante vêm antes de abrir o inventário, para agir em qualquer estado (perguntas 510, 516 e 800).
- **Estado interno também valida:** os records que só guardam tempo ou ângulo rejeitam valor inválido, como os demais value objects (pergunta 526).
- **Fila sem remoção:** um robô se informa a cada tick, então o que saiu do mundo perde o lugar sem ninguém precisar tirá-lo (perguntas 85 e 86).
- **Cheio espera um momento:** o robô cheio fica cerca de 3 s antes de sair, para o player ver a tela completa (pergunta 312).
- **Vida igual em todos os tiers:** a vida por tick segue a vida máxima, então todos levam o mesmo tempo para encher (pergunta 63).
- **Reserva do slot:** um item só é consumido quando a energia dele cabe, e a reserva nunca some, para a transferência não criar nem perder FE (regra 13 e pergunta 64).
- **Excedente:** se o config reduzir os slots, o que não couber sobra, e quem chama o larga no chão. Nada é apagado (pergunta 187).
- **Vínculo apontando para o próprio robô:** não conta como outro robô seguindo. Assim, um vínculo antigo não deixa o robô desligado.
- **Crítico só fora da base:** na tela, a bateria crítica vence seguindo e ativo, mas hibernando vale o estado da base, como nas imagens das telas (perguntas 681 e 686). O inativo dá `OFF` em vez de falhar, porque a tela ainda pode ser desenhada uma vez antes de fechar (pergunta 684).
- **Número arredondado para baixo:** a tela nunca mostra mais energia do que existe. As taxas usam o separador de milhar da localidade raiz, para não mudar com o idioma do player (pergunta 687).
- **Metades sem sinal:** o jogo envia cada valor da tela como um `short`, que chega com sinal, então `join` só usa os 16 bits de cada metade. Acima de 32 bits, mostra o maior valor possível, muito acima de qualquer bateria.
- **Ordem do painel:** desligado vem primeiro, depois a dimensão sem sol e a bateria cheia, que não dependem do céu, e só então o céu coberto e a noite (pergunta 693).
- **Causas como no gasto:** peso, água e subida só aparecem andando, a mesma regra de `ConsumptionExtras`, para a tela nunca citar o que não custou (pergunta 693).
- **Slot antes do painel:** na linha de recarga, o slot vence o painel, e na base só o Dock conta (pergunta 693).
- **Tempo da vida pela parte que falta:** a base enche a vida inteira num tempo fixo, então uma perda menor leva menos tempo. Menos de um minuto aparece como tal (pergunta 693).
- **Entrada pela média:** cabos entregam energia aos saltos, então a média de um segundo evita um número que pula (pergunta 693).
- **Velocidade em porcentagem do tier:** o mesmo valor serve em qualquer tier e acompanha o upgrade. Os degraus descem de 100% de passo em passo, e o mínimo é sempre o último, mesmo fora do passo, para um config qualquer do pack continuar válido. Um valor salvo fora dos degraus vai para o mais próximo, sem perder o save (perguntas 1259 e 1266).
- **Fugir vem primeiro:** em chamas, na lava ou sobre um bloco que fere, a velocidade escolhida não vale, como o redutor do inventário cheio, porque andar devagar no perigo pode matar o Cubit (pergunta 1268).
- **Baú com mais do mesmo item:** o Organizador junta o que é igual, com os mesmos dados, para a base ficar arrumada. Sem lugar, ele fica com o item, porque largar no chão poderia perder o item (perguntas 1292 e 1293).
- **Pedido sem passar da quantidade:** o destino recebe só o que falta, para uma fornalha não ficar cheia de um combustível que o player queria em outro lugar (pergunta 1294).
- **Portas do dono:** o Cubit só abre a porta que o dono marcou, porque num servidor a porta de outro player não é dele. As marcas são do dono, e não de um Cubit, para valerem para todos os Cubits dele. Ele só fecha a porta que abriu, para não fechar a que o dono deixou aberta (perguntas 1360, 1364 e 1371).
- **O Dock decide a recarga na base:** um Cubit na base carrega na taxa do tier do Dock, e não na do tier dele, para o upgrade do Dock valer para qualquer Cubit. O contêiner do inventário tem sempre os 54 slots do Ultimate, e o tier abre os primeiros, para nada mudar de lugar ao subir (perguntas 1374 e 1375).
- **A mesa paga no fim:** os materiais e a energia de um craft só saem quando ele termina, e de uma vez, para uma pausa no meio nunca perder nada (regra 13 e pergunta 1435).
- **A primeira causa é a mostrada:** a mesa olha o pedido, o sinal de redstone, os materiais, a receita, a energia e a saída, nessa ordem, e mostra a primeira que falta. A pausa guarda o progresso, e a mesa volta sozinha (perguntas 1443 e 1448).
- **Pedido de zero ao maior do tier:** escolher uma receita só a marca, com o pedido em zero, para nada ser feito por acidente, e − e + andam de 1 em 1, ou de 8 em 8 com Shift. Baixar até o que já está pronto encerra o pedido, e um tier ou um config menor corta o total (perguntas 1429, 1444 e 1506).
- **Comparador arredonda para cima:** o sinal vai de 15 no início do pedido a 0 no fim, e o último item ainda acende 1 (pergunta 1453).
- **XP que vira FE não volta:** o `XpConversion` só vai de pontos a energia, para nenhum modo, tela ou bloco fazer XP de energia (pergunta 2111).
- **Ponto inteiro na conversão:** um ponto só sai do tanque quando a energia dele cabe inteira na bateria, para nem XP nem FE se perderem (regra 13).
- **XP do player e do cano fora dos modos:** os dois entram direto no tanque e não passam pelo `DockXpRules`, para nenhum modo apagar o que veio de propósito (perguntas 1936 e 2072).

## Estados

Os documentos definem três estados: ativo, inativo e hibernação. No código, o inativo se divide em dois, porque a causa muda o comportamento:

| `PowerState` | Estado | Acesso ao inventário | Como sai |
|---|---|---|---|
| `ACTIVE` | Ativo | Sim | Bateria zerada, troca de robô, perda do lugar de seguir ou entrada na base |
| `INACTIVE_EXHAUSTED` | Inativo, sem bateria | Não | Sozinho, ao atingir a carga mínima |
| `INACTIVE_SWITCHED_OFF` | Inativo, desligado com carga | Não | Clique direito, se houver carga |
| `HIBERNATING` | Hibernação, na base | Sim | Ao sair da base |

O desligamento pelo Tablet, que substitui o controle remoto (perguntas 56 e 1021), entra na Fase 4.2, junto do Tablet.
