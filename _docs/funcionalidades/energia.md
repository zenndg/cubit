# Energia

Números: [`tiers.md`](../tiers.md#energia) e [valores gerais](../tiers.md#valores-gerais).

## Bateria

- Uma bateria interna alimenta o sistema.
- É um recurso a gerenciar: o robô precisa voltar para carregar.
- As barras do item e da tela mostram ao menos um risco enquanto houver carga, e só ficam vazias com 0 FE.

## Estados do Cubit

| Estado | Quando acontece | Painel solar | O que o player pode fazer |
|---|---|---|---|
| **Ativo** | Ligado, fora da base | Funciona | Todas as interações |
| **Inativo** | Desligado, por exemplo sem bateria | Desligado | Só recolher, colocar energia no slot exclusivo ou, com bateria, ligar com o clique direito, exceto se tiver sido desligado pelo controle |
| **Hibernação** | Na base (Induction Dock) | Desligado | Usar o inventário e os slots exclusivos, ou recolher |

- **Hibernação não é o mesmo que desligado:** o Cubit carrega e continua acessível. Fora da base, ela entra na Fase 5, conforme [`trabalhos.md`](trabalhos.md#hibernação-fora-do-dock).
- **Base:** é sempre o Induction Dock, o bloco fixo de recarga.
- **Religar:** sem bateria, com a carga mínima ele volta sozinho a ficar ativo.
- **Sem bateria na base:** entra em hibernação logo, sem esperar a carga mínima.
- **Desligado pela tela:** o botão `Power` da aba Settings desliga o Cubit, que fica como o desligado com carga, e libera o lugar de quem segue. Ligar de novo é pelo clique direito, e ele volta a seguir se ninguém estiver seguindo (perguntas 857 e 858).
- **Desligado pelo Tablet:** segue as regras do inativo, mesmo com bateria, como pelo botão `Power` da aba Settings, e o clique direito no Cubit o religa (perguntas 56, 1021 e 1033).
- **Sons:** o Cubit tem um som ao ligar, um ao desligar e um enquanto é recarregado. Eles entram na Fase 3, e os detalhes estão em [`visual.md`](../conceito/visual.md#sons).

## Consumo

Os números estão em [`tiers.md`](../tiers.md#consumo-por-atividade), e o consumo só vale com o robô ativo.

- **Andar:** gasta o valor do tier a cada tick com a rota em andamento e deslocamento real, inclusive ao fugir do fogo e da lava. Ser empurrado não gasta, e rota sem avanço também não.
- **Inventário cheio:** com todos os slots ocupados, mesmo com pilhas incompletas, andar custa um extra, e a velocidade cai. Em chamas ou na lava, a velocidade não cai, porque fugir vem primeiro.
- **Velocidade escolhida:** com o [controle de velocidade](movimento.md#controle-de-velocidade) abaixo de 100%, o Cubit leva mais ticks no mesmo caminho e, por isso, gasta mais energia nele (pergunta 1259).
- **Água:** andar em contato com a água custa um extra, inclusive numa poça rasa e ao fugir do fogo.
- **Subir:** cada bloco de altura subido na própria rota custa um extra, proporcional à altura. Descer não devolve energia.
- **Condição negativa:** fogo, lava, [blocos que ferem](movimento.md#sobre-um-bloco-que-fere) e efeitos nocivos do jogo ou de outros mods custam um extra a cada tick, mesmo parado. Várias juntas cobram um extra só, e um golpe não conta. O extra pode zerar a bateria. Aí o robô desliga e, em chamas, queima parado.
- **Lanterna:** acesa, custa um extra a cada tick, mesmo parado, e pode zerar a bateria. A claridade da tela não gasta. Os detalhes estão em [`sensores.md`](sensores.md#lanterna).
- **Ímã:** do Advanced em diante, custa um valor fixo por item que ele puxa, e nada sem itens por perto. Sem energia suficiente, não puxa, e a coleta por contato, que não gasta, continua. Os detalhes estão em [`inventario.md`](inventario.md#coleta).

## Como o Cubit carrega

Só existem cinco formas, e nenhuma outra:

1. **Induction Dock**
2. **Power Bank** no slot exclusivo
3. **Redstone** no slot exclusivo
4. **Painel solar**
5. **[Recharge Splash](#recharge-splash)**, arremessada pelo dono

## Painel solar

Funciona como um carregador solar real.

- **Quando carrega:** só com o Cubit ativo e durante o dia. Inativo ou em hibernação, o painel fica desligado.
- **À noite:** só carrega nos dois últimos tiers, Elite e Ultimate.
- **Blocos:** coberto por um bloco opaco, não carrega. Com o céu aberto ou sob vidro, carrega 100%.
- **Clima:** não tem efeito.
- **Dimensões:** no Nether e no End, não carrega.
- **Sinal:** enquanto o painel enche a bateria, a célula clareia e escurece devagar. Com a bateria cheia, o sinal para. O painel não toca o som de recarga, que é da base e do slot.
- **Chave:** `Solar panel`, na aba Settings da tela do Cubit. Começa ligada, também em saves antigos, e fica no item. Desligada, o painel não carrega nem pulsa (perguntas 859 e 860).

## Induction Dock

- É a base de recarga do Cubit, inspirada nas bases de robôs aspiradores. Na base, o Cubit fica em hibernação.
- **Formato:** o da [prancha de conceito](../imagens/conceito/induction-dock-prancha.png): torre atrás, com tela, e placa em U na frente, virada para o player que coloca. O Cubit sobe na placa, de costas para a torre. Ocupa 2 blocos de altura, como uma porta, e precisa do bloco de cima livre para ser colocado. Quebrar uma parte quebra as duas.
- **Tela:** desligada, sem energia e sem Cubit; ociosa, com energia e sem Cubit; carregando, com o Cubit recebendo; completa, com a bateria dele cheia; sem energia, com o Cubit na base e o estoque zerado.
- **Energia:** recebe por cabos de outros mods, em **FE**, em qualquer face, ou com redstone em um slot próprio, também por funil. Nenhuma energia sai por cabo. Pelos funis, só sai o que a [automação do inventário](#inventário-do-dock) permite.
- **Estoque:** guarda a energia do tier do Dock, de 300 mil a 16 mi FE (pergunta 1374). Um item de redstone só vira energia quando couber inteiro no estoque. Sem FE, o Cubit fica hibernando.
- **Slot de entrada:** aceita redstone, que vira estoque, ou uma Power Bank, que descarrega no estoque pelo mesmo limite de entrada dos cabos e aparece em `Input +X FE/t`. Vazia, ela fica parada no slot (perguntas 919, 920 e 923). O slot de carga saiu, e o inventário assumiu o papel dele (pergunta 950).
- **Clique:** o clique direito com redstone na mão a põe no slot de entrada. Com uma Power Bank, uma Toolbox ou um Damaged Cubit na mão, o item vai para o inventário, juntando nas pilhas e nos slots livres, e o que não couber fica na mão. Com o item do Cubit, o robô vai para a base, como sempre. Só o dono (perguntas 959 e 968, que mudam a 924).
- **Inventário:** guarda os itens do mod e carrega as Power Banks. Veja [Inventário do Dock](#inventário-do-dock).
- **Recarga do Cubit:** na taxa do tier do Dock, de 500 a 5.000 FE/t, sem contar o tier do Cubit (pergunta 1375). Veja [Tiers do Dock](#tiers-do-dock).
- **Vida:** recupera a vida do Cubit durante a hibernação, sem gastar FE, mesmo com o estoque zerado.
- **Retorno automático:** serve de base para ele, na Fase 4.

### Dono e tela

- **Dono:** quem coloca o Dock. Só o dono usa o Dock e abre a tela, e só os Cubits dele entram.
- **Clique direito:** chama o Cubit ou, com um na base, o libera. Com redstone na mão, a pilha entra no slot.
- **Shift + clique direito:** abre a tela própria do Dock, descrita em [`telas.md`](telas.md#tela-do-induction-dock), com o botão de chamar o Cubit mais próximo (perguntas 371 e 678).
- **Quebrar:** só o dono, ou um player no modo criativo com permissão de operador, para remover um Dock abandonado. Sem dono, como num save sem a chave ou colocado por algo que não é um player, só o operador o remove, e o log avisa ao carregar (pergunta 524). Explosões e pistões não o afetam. O item guarda a energia, o tier e o inventário e empilha em 1 (pergunta 1373), o slot de entrada cai no chão, e o Cubit sai como numa liberação (pergunta 954). O dono do bloco novo é quem o coloca.

### Inventário do Dock

Entra no item 11 da Fase 4.1 (issue #168, perguntas 947 a 959).

- **Onde:** na aba `Storage` da tela do Dock, descrita em [`telas.md`](telas.md#aba-storage).
- **Tamanho:** o do tier do Dock: 27 slots no Base, como um baú simples do jogo, 36 no Advanced, 45 no Elite e 54 no Ultimate (pergunta 1374).
- **O que aceita:** todos os itens do mod, cada um na pilha dele, inclusive o item do Cubit e o Damaged Cubit. Os itens de fora do mod ficam fora (pergunta 1361, que muda a 965). O Cubit e o Damaged Cubit só ficam guardados, com todo o estado no item, sem recarga nem conserto (pergunta 966).
- **Recarga:** o estoque carrega uma Power Bank por vez, a primeira não cheia na ordem dos slots, na taxa do tier do Dock, até ela encher (pergunta 1376, que muda a 949), sem criar nem perder FE (pergunta 949 e regra 13). Só carrega sem Cubit na base ou com o Cubit da base de bateria cheia. Com o Cubit ainda carregando, nenhuma Power Bank recebe (pergunta 952, que muda a 906).
- **Automação:** por qualquer face, funis e cabos de itens põem os itens do mod no inventário. A Power Bank só sai cheia, e os outros itens saem a qualquer momento, também por qualquer face (pergunta 967). O Cubit e o Damaged Cubit também entram pela automação. A redstone continua entrando no slot de entrada, e a automação nunca põe a Power Bank nele (perguntas 925, 951 e 953).
- **Quebrar:** o inventário fica no item do Dock e volta ao colocá-lo (pergunta 954).
- **Saves antigos:** a Power Bank que estava no slot de carga vai para o primeiro slot do inventário ao carregar o mundo, com o registro no log (pergunta 955).

### Tiers do Dock

O Induction Dock sobe de tier com os mesmos Modules do Cubit (issue #225, perguntas 1373 a 1380). Os números estão em [`tiers.md`](../tiers.md#induction-dock).

- **Subir:** o clique direito no Dock com o Module do tier seguinte sobe o Dock, sem pular etapas, e consome o Module, como no Cubit. O Module que não serve não é consumido, e um texto explica o motivo (pergunta 1377).
- **Dono:** só o dono sobe o Dock. Para outro player, nada acontece (pergunta 1378).
- **O que muda com o tier:** o estoque, os slots do inventário e a recarga do Cubit e da Power Bank guardada. A entrada, por cabos e redstone, fica em 5.000 FE/t em todos (pergunta 1374).
- **Slots novos:** ao subir, os slots a mais aparecem vazios, e os itens continuam onde estavam.
- **Recolher:** o item do Dock guarda o tier, com a energia e o inventário, e o Dock volta no mesmo tier ao ser colocado (pergunta 1373).
- **Faixa:** uma faixa em volta da torre, logo acima da placa, como o colar do Cubit, fica na cor do tier, para ser fácil de ver: cinza, azul, roxo e dourado. A borda em volta do topo, da pergunta 1871, saiu quando o painel foi para o 3º bloco (perguntas 1379, 1389 e 1899).
- **Ultimate:** o corpo do Dock fica em netherite, como a Ultimate Power Bank e o Ultimate Module (pergunta 1380).
- **Tela e Jade:** a tela do Dock e o Jade mostram o tier, na cor dele.
- **Saves antigos:** um Dock sem o tier, colocado antes, vale como Base, e o estoque dele passa a caber 300 mil FE.

### Painel solar do Dock

Todo Induction Dock tem um painel solar embutido, que ele monta e guarda sozinho (issue #246, perguntas 1852 a 1866, com o conceito autorizado na 1872). O Solar Induction Dock, o Dock Solar Panel e a receita que os juntava, da issue #227, saem do mod sem migração, porque nunca foram publicados (pergunta 1854). Os números estão em [`tiers.md`](../tiers.md#induction-dock).

- **Gera:** de dia, a céu aberto e sem chuva, com o painel montado por completo, o painel põe no estoque 25, 100, 500 ou 1.400 FE/t, pelo tier do Dock, também no Base. A geração soma à entrada dos cabos e da redstone, sem passar do estoque (pergunta 1855).
- **Três blocos:** o Dock ocupa sempre 3 blocos de altura: a base e a torre nos 2 de baixo, como antes, e um bloco próprio e invisível no 3º, `cubit:induction_dock_top`, onde o painel monta. Para colocar o Dock, os 3 blocos precisam estar livres, e quebrar qualquer parte quebra o Dock inteiro, que sai como item. O estado de bloco das duas partes de baixo não muda (perguntas 1892 e 1896).
- **Colisão do topo:** só com o painel montado, uma placa fina no alto, que segura quem subir nela. Com o painel guardado, o 3º bloco fica vazio e se atravessa, mas continua reservado ao Dock, sem aceitar outro bloco (pergunta 1898).
- **Sol:** a leitura do céu é a de antes, um bloco mais alta, acima do 3º bloco. Dentro de casa, com um bloco opaco acima do painel, com chuva ou tempestade, à noite, no Nether e no End, não há sol. O vidro e os outros blocos translúcidos acima do painel deixam passar a luz (perguntas 1398, 1853 e 1901).
- **Montar:** depois de 10 s seguidos com sol, o Dock monta o painel. **Guardar:** depois de 10 s seguidos sem sol, inclusive ao anoitecer, ele o guarda no armário. A espera evita abrir e fechar a cada nuvem de chuva (perguntas 1856 e 1862).
- **Torre:** as mesmas medidas de antes, 9 px de largura, 4 px de fundo e 1,75 bloco de altura, com o mesmo encaixe e a mesma colisão do Cubit na placa (perguntas 1887 e 1902).
- **Armário:** fica no centro da parte de trás da torre e guarda as duas partes do painel, cada uma um pacote de lâminas dobradas em sanfona, em pé, uma atrás da outra, com dois braços articulados. As duas portas deslizam para os lados e somem por inteiro dentro da parede, como portas de elevador, e abrem mesmo com um bloco encostado atrás do Dock (perguntas 1852, 1858, 1889 e 1894).
- **Painel montado:** uma placa fina, como um painel solar de verdade, e não um bloco, com 16×16 px, o bloco inteiro, centrada sobre o Dock, no 3º bloco. Fica inclinada uns 15° para a frente, com o lado mais alto nas costas e o topo nos 3 blocos exatos. Os dois braços ficam à vista por baixo, saindo do armário, passando sobre a torre e segurando o painel pelas bordas (perguntas 1891, 1893, 1903 e 1904).
- **Montagem,** como um robô se transformando, em cerca de 6 s, e a desmontagem na ordem contrária (perguntas 1891, 1894 e 1895):
  1. A luz das portas pisca em amarelo, e as portas deslizam.
  2. Os dois braços saem com os pacotes de lâminas em pé.
  3. Os braços sobem por cima da torre até o 3º bloco.
  4. Giram os pacotes para a horizontal, um para cada lado.
  5. As lâminas se abrem em sanfona, uma a uma, e as dobradiças e as células vão aparecendo.
  6. As duas metades se encontram no centro, com o clique da trava.
  7. O painel inclina uns 15°, a luz fica verde, e as células começam a refletir e pulsar. As portas ficam abertas, e os braços continuam segurando o painel.
- **Luz:** um pequeno ponto na borda de cada porta, verde com o painel montado, vermelho com as portas fechadas e o painel guardado, e piscando em amarelo durante o movimento. O painel não troca de cor (perguntas 1853 e 1863).
- **Sinal:** com o painel montado e gerando, a face dele reflete a luz e clareia e escurece devagar, com o mesmo efeito do painel do Cubit (perguntas 1853 e 1891).
- **No meio do movimento:** o servidor decide e salva só se o painel está montado ou guardado, e o cliente toca a animação. Ao carregar o mundo ou o chunk no meio de um movimento, o Dock aparece direto no destino. Quebrado, o painel some com o Dock, que sai como item, como hoje (pergunta 1864).
- **Sons:** três sons próprios, descritos em [`sons.md`](../conceito/sons.md#induction-dock): a abertura e a montagem, a desmontagem e o fechamento, e a trava (pergunta 1861).
- **Aba Solar:** em todo Dock, com o que mostrava no Dock Solar (gerando ou parado, o motivo, a geração de agora e a máxima do tier) e o estado do painel: Guardado, Montando, Montado ou Guardando, com a luz (pergunta 1865). Num Dock sem espaço para o 3º bloco, ela mostra que falta espaço para o painel (pergunta 1897).
- **Jade:** mostra a geração, como `Solar +25 FE/t`, ou `Solar: no sun`, e o estado do painel (pergunta 1865).
- **Prancha conceito:** uma prancha nova em três imagens, por prompt, com a arte do desenvolvedor: as vistas, a transformação passo a passo e os detalhes da sanfona, dos braços e da trava. Aceita a prancha, e antes do teste no jogo, o agente mostra prévias do modelo renderizadas por script, de vários lados. A prancha da pergunta 1884 deixa de valer, porque mostrava o painel pequeno, rente ao topo (perguntas 1891 e 1900).
- **Modelo:** o Dock usa o GeckoLib, como a Engineering Desk, no mundo e no inventário, com o modelo e o prompt da textura com o gabarito UV (pergunta 1860). A tela, a linha abaixo dela e a faixa da placa mostram as mesmas cinco imagens de status de antes, desenhadas por uma camada própria e acesas no escuro (pergunta 1886).
- **O resto:** tudo o que o Dock faz continua igual: recebe o Cubit, hiberna, fila, inventário, cabos e os tiers pelos Modules.
- **Saves antigos:** um Dock de antes vem com o painel guardado. Ao carregar, se o espaço acima dele estiver vazio, ele ganha o 3º bloco sozinho e monta o painel quando houver sol. Se o espaço estiver ocupado, ele continua funcionando sem o painel, até o espaço ficar livre. Nada é quebrado nem perdido (pergunta 1897).

### Nome do Dock

O Dock tem um nome, como o Cubit (perguntas 1867 a 1870).

- **Padrão:** `Dock` seguido dos 8 primeiros dígitos de um UUID próprio do Dock, como `Dock 3f9a2c1b`. O UUID é sorteado ao colocar o Dock, salvo no NBT e no item, e não muda ao quebrar e colocar de novo. Um Dock de um mundo antigo, sem UUID, sorteia o seu ao carregar, sem mudar mais nada (regra 18).
- **Renomear:** um botão `Rename` na tela do Dock, com as regras do Rename do Cubit: até 50 caracteres, espaços das pontas cortados, só o dono, conferido no servidor. Um nome vazio volta ao padrão, para o Dock nunca ficar sem nome. A etiqueta do jogo também renomeia.
- **Onde aparece:** no título da tela do Dock, no lugar de `Induction Dock`; no Jade; no app Dock do Tablet e no Dock preferido; no nome do item do Dock quebrado; e nos avisos que citam um Dock, como os do retorno automático e do XP.

### Ir para a base e sair dela

- **Ir:** o player clica com o botão direito no Dock, e o Cubit ligado mais próximo, dentro do raio do retorno automático, vai até ele, esteja seguindo ou em área. Colocar o item do Cubit em cima do Dock também funciona. Num Dock ocupado ou de outro player, nada acontece, e o item continua na mão.
- **Sem conseguir chegar:** sem rota ou preso, teleporta para o Dock. Se o chunk estiver descarregado ou houver um bloqueio em cima, desiste e volta ao que fazia.
- **Fechar o jogo:** só a hibernação é salva. A caminho ou aguardando, o Cubit volta ao que fazia ao carregar de novo. Se o Dock sumiu, ele acorda como numa liberação.
- **Sair:** o Cubit sai sozinho cerca de 3 s depois de a bateria e a vida estarem cheias. Com a bateria cheia e a vida ainda subindo, a tela fica no estado completa e mostra quanto falta para a vida encher (pergunta 963, que muda a 55). Antes disso, clicar no Dock o libera. Ao sair, volta ao que fazia: a área ou seguir o player. Se outro assumiu o lugar de seguir, fica parado e desligado.
- **Dono offline:** o Cubit cheio continua na base e sai quando o dono entra. Se o Dock sumir nesse período, ele acorda desligado, e um clique o liga.

### Base ocupada

Cada Dock recebe um Cubit por vez. Se a base estiver ocupada:

1. O Cubit vai para a base livre mais próxima dentro do raio.
2. Se todas estiverem ocupadas, ele para na frente da base e aguarda, ainda no estado ativo.
3. Quando a base libera, entra o que chegou primeiro.

## Slot exclusivo de energia

O slot fica na tela do Cubit, ao lado das barras ([`telas.md`](telas.md#aba-inventory)). O Cubit extrai a energia da Power Bank ou da redstone colocada no slot para a bateria interna, na velocidade de recarga do tier. Cada item extraído vira uma reserva, salva com o slot, para nada se perder se o robô for recolhido no meio da recarga. O slot existe em todos os tiers e aceita um dos dois por vez. A redstone é consumida um item por vez, e só quando a energia couber na bateria.

### Power Bank

- Bateria portátil que o player carrega no próprio inventário.
- Existe uma Power Bank para cada tier. A capacidade de cada uma equivale a uma porcentagem da bateria interna do tier correspondente.
- Entra na Fase 4. A redstone no slot já funciona na Fase 2.
- Funciona em um Cubit de qualquer tier, na velocidade de recarga do tier do Cubit.
- Não empilha: cada uma ocupa um slot.
- Sai vazia do craft e recarrega só no inventário do Induction Dock. Não tem capability de FE: outros mods não a carregam nem tiram energia dela. Ela entrega energia ao Cubit e, no slot de entrada, ao estoque do Dock (perguntas 905 e 919, que mudam a 42).
- **Carga:** fica no componente `cubit:power_bank_energy`, como energia inteira, e aparece na barra verde e na dica do item, como `105k / 210k FE` (perguntas 903 e 909). A capacidade é a `power_bank_capacity` do tier dela.
- **No slot do Cubit:** entrega na velocidade de recarga do tier do Cubit, até a bateria encher ou ela esvaziar. Vazia, continua no slot (pergunta 904).
- **No Dock:** no inventário, recebe na velocidade de recarga do Base, uma por vez, sem Cubit na base ou com o Cubit de bateria cheia (perguntas 949 e 952). No slot de entrada, ela descarrega no estoque (pergunta 920).

### Recarregar um Cubit desligado

1. Com o Cubit desligado e a Power Bank ou a redstone na mão, o player clica com o botão direito no Cubit.
2. O item entra sozinho no slot exclusivo, e a recarga começa.
3. Com a carga mínima, o Cubit liga.
4. A partir daí, o player pode acessá-lo, remover o item e fazer as outras interações.

Com redstone na mão, o clique num Cubit desligado põe a pilha no slot, mesmo com carga. Para ligá-lo, basta clicar sem redstone na mão.

### Recarregar o item pela Engineering Desk

Entra na Fase 4, junto da Power Bank.

O item com a bateria abaixo da carga de religar, como zerada, e com vida pode ser colocado no mundo, mas o robô fica desligado, com a interação limitada. Depois de recolhê-lo, o player pode usar esta receita.

- **Como:** combine o item do Cubit com uma Power Bank, em qualquer posição da grade da [Engineering Desk](mesa-de-engenharia.md) (pergunta 1427). A Power Bank volta ao inventário de saída da mesa com a carga restante.
- **Quando:** só com a bateria do Cubit abaixo da carga de religar, e a Power Bank precisa ter o que falta. Sem isso, a receita não aparece. O `Damaged Cubit` não vale.
- **Quanto:** só o que falta para a carga de religar, sobre a bateria do Cubit. Assim, ele volta a funcionar ao ser colocado. A Power Bank perde exatamente essa quantidade, sem criar nem perder FE.
- **O que se mantém:** o Cubit não perde mais nada do estado, e a Power Bank continua na grade, com a carga restante.

## Recharge Splash

![Recharge Splash quebrando ao lado do Cubit](../imagens/conceito/recharge-splash.png)

Uma mistura de arremesso, `cubit:recharge_splash`, que o dono arremessa nos próprios Cubits para recuperar parte da vida e da energia deles (perguntas 1393 a 1419). A receita está em [`itens.md`](../itens.md#recharge-splash), e os números, em [`tiers.md`](../tiers.md#valores-gerais).

- **Arremesso:** como a poção de arremesso do jogo, com o clique direito mirando no chão ou no ar ao lado do Cubit. Com a mira no Cubit, o clique abre a tela dele, como hoje (pergunta 1422). O frasco se quebra no primeiro bloco ou entidade que atinge, com o respingo, e se gasta mesmo sem nenhum Cubit perto. O dispenser não a arremessa: só solta o item (pergunta 1409).
- **Quem recebe:** todo Cubit do player que arremessou, a até 4 blocos do ponto de impacto, numa esfera. O raio só conta no impacto: o Cubit que se afasta depois continua recebendo até o fim (perguntas 1402 e 1418).
- **Só no Cubit:** a mistura não dá vida nem efeito a players, mobs e outras entidades, nem aos Cubits de outros donos, que só veem o respingo (perguntas 1408 e 1409).
- **Quanto:** 20% da vida máxima e 15% da bateria do tier do Cubit atingido, com a energia arredondada para baixo. No Base, são 5 de vida e 45 mil FE; no Ultimate, 40 de vida e 1,2 mi FE. O Cubit nunca passa da vida máxima nem da bateria cheia, e o que não cabe se perde (perguntas 1401 e 1405).
- **Aos poucos:** a vida e a energia entram em partes iguais a cada tick, ao longo de 3 s, 60 ticks. O resto da divisão da energia entra no último tick, e a soma dá exatamente o total (perguntas 1414 e 1415).
- **Uma por vez:** enquanto recebe uma mistura, o Cubit não recebe outra. A segunda não lhe dá nada, e o player que arremessou vê na barra de ação `Cubit is still recharging: wait 2 s`, com os segundos que faltam. Os outros Cubits dele no raio, que não estejam recebendo, recebem normalmente (perguntas 1412, 1416 e 1419).
- **Estados:** vale para o Cubit ativo, sem carga, desligado pelo player e hibernando no Dock. Sem carga, ele religa ao passar de 5%. Desligado pelo player, continua desligado. No Dock, recebe e continua lá. O Damaged Cubit é um item e não recebe (pergunta 1407).
- **Interrupção:** o que falta entrar não é salvo. Se o Cubit for recolhido, morrer ou o mundo fechar no meio dos 3 s, ele fica com o que já recebeu (pergunta 1417).
- **Efeitos do jogo:** a mistura não é um efeito de poção e não mexe nos efeitos que o Cubit já tem. Ele continua recebendo os efeitos do jogo e de outros mods, com o gasto da [condição negativa](#consumo) (perguntas 1403 e 1404).
- **Visual e som:** o respingo solta partículas vermelhas e faíscas azuis. O Cubit que começa a receber solta faíscas, toca uma vez o som de recarga e troca o rosto para a expressão feliz padrão (pergunta 1421).
- **Tooltip:** o item explica que a vida e a energia entram em 3 s e que cada Cubit recebe uma mistura por vez (pergunta 1419).

## Retorno automático

Entra com o item 15 da Fase 4.2, antes do modo área (perguntas 1065 a 1069).

- **Quando:** com a chave ligada, o Cubit ativo que segue o player, ou que está numa área, chega à bateria do retorno, `return_charge_percent`, 10% em todos os tiers ([`tiers.md`](../tiers.md#energia)).
- **Para onde:** como o `Ir para o Dock`: o Dock preferido, se estiver na mesma dimensão, carregado e no raio de chamada do tier, senão o Dock livre mais próximo nesse raio, com a fila de hoje (perguntas 1041, 1042 e 1067).
- **Sem Dock ao alcance:** o Cubit continua o que fazia e tenta de novo a cada intervalo da busca, até o crítico desligá-lo, como hoje (pergunta 1067).
- **Texto:** `I need to recharge` sai uma vez, quando o retorno começa, e só do Cubit que segue (perguntas 457 e 1068).
- **Ao sair do Dock:** com a carga cheia, o que seguia volta a seguir, pela regra de hoje, e o que estava numa área volta para ela (pergunta 1068).
- **Patrulheiro:** com o card, o retorno vale com 5% no Elite e 3% no Ultimate, em `patroller_return_charge_percent`, só depois do combate e com um aviso ao dono na barra de ação, no lugar do `I need to recharge`. As regras estão em [`patrulheiro.md`](trabalhos/patrulheiro.md#bateria) (perguntas 1688 a 1690 e 1699).
- **Chave:** `Auto return` / `Retorno automático`, desde o Base, ligada por padrão e guardada no item, na aba Settings da tela do Cubit e na aba Recursos do Tablet (pergunta 1066).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-23 01:17 | Refaz o painel do Dock: 3 blocos, o painel fino suspenso pelos braços no 3º bloco, a transformação em sanfona, a faixa acima da placa e a prancha nova (perguntas 1891 a 1905). |
| Zennd | 2026-11-23 00:51 | Acrescenta o armário estreito, as medidas e a tela do modelo GeckoLib do Dock (perguntas 1886, 1887 e 1889). |
| Zennd | 2026-11-23 00:33 | Acrescenta a prancha conceito do painel do Dock e a subida inclinando no passo 3 (perguntas 1883 a 1885). |
| Zennd | 2026-11-22 23:44 | Troca o Dock Solar pelo painel embutido em todo Dock, com o armário animado, e acrescenta o nome do Dock (perguntas 1852 a 1871). |
| Zennd | 2026-11-22 14:50 | Acrescenta o retorno do Patrulheiro (perguntas 1688 a 1690 e 1699). |
| Zennd | 2026-11-21 12:50 | Passa a recarga do item para a Engineering Desk (pergunta 1427). |
| Marcelo Fabiano | 2026-11-21 11:30 | Adiciona a Recharge Splash e a imagem dela (perguntas 1399 a 1423). |
| Marcelo Fabiano | 2026-11-21 09:50 | Adiciona a aba Solar e o vidro sobre o painel (perguntas 1395 a 1398). |
| Marcelo Fabiano | 2026-11-21 09:10 | Adiciona o pulso e o visual do painel do Dock Solar (pergunta 1392). |
| Marcelo Fabiano | 2026-11-21 08:20 | Adiciona o Dock Solar e leva a faixa do tier para o topo da torre (perguntas 1384 a 1389). |
| Marcelo Fabiano | 2026-11-21 06:15 | Adiciona os tiers do Dock e o inventário com todos os itens do mod (perguntas 1373 a 1380). |
| Marcelo Fabiano | 2026-11-19 20:18 | Cita o gasto maior com a velocidade escolhida abaixo de 100% (pergunta 1259). |
| Marcelo Fabiano | 2026-11-14 10:00 | Define o retorno automático (perguntas 1065 a 1069). |
| Marcelo Fabiano | 2026-11-13 18:50 | Faz o desligado pelo Tablet voltar pelo clique, como o Power. |
| Marcelo Fabiano | 2026-11-13 16:40 | Troca o controle remoto pelo Tablet. |
| Marcelo Fabiano | 2026-11-13 13:00 | Faz o Cubit sair do Dock só com a bateria e a vida cheias. |
| Marcelo Fabiano | 2026-11-13 12:50 | Aceita o Cubit e o Damaged Cubit no inventário do Dock. |
| Marcelo Fabiano | 2026-11-13 11:30 | Troca o slot de carga do Dock pelo inventário, na aba Storage. |
| Marcelo Fabiano | 2026-11-12 23:10 | Divide o slot do Dock em entrada e carga para a Power Bank. |
| Marcelo Fabiano | 2026-11-12 22:30 | Define a Power Bank só no Dock e no Cubit, a carga, o slot e a divisão no Dock. |
| Marcelo Fabiano | 2026-11-12 21:45 | Adiciona o consumo do ímã. |
| Marcelo Fabiano | 2026-11-12 17:20 | Define a chave do painel solar e o desligar pela tela. |
| Marcelo Fabiano | 2026-11-07 23:42 | Leva a chave do painel solar para a Fase 4. |
| Marcelo Fabiano | 2026-11-07 23:12 | Aponta a tela do Dock para as telas próprias. |
| Marcelo Fabiano | 2026-11-07 23:11 | Aponta a tela do Dock para as telas próprias. |
| Marcelo Fabiano | 2026-11-07 13:51 | Define quem quebra o Dock sem dono. |
| Marcelo Fabiano | 2026-11-06 15:05 | Detalha o vidro, o sinal e a chave do painel solar. |
| Marcelo Fabiano | 2026-11-06 13:34 | Adiciona o consumo da lanterna. |
| Marcelo Fabiano | 2026-11-06 11:15 | Limita o texto do retorno ao Cubit que segue. |
| Marcelo Fabiano | 2026-11-06 11:10 | Troca 'especialidade' por 'trabalho' e 'card de especialidade' por 'card de Aprendizado de Máquina'. |
| Marcelo Fabiano | 2026-11-05 23:49 | Cita a janela personalizada do Dock. |
| Marcelo Fabiano | 2026-11-05 21:30 | Define a saída da base com o dono offline. |
| Marcelo Fabiano | 2026-11-05 21:02 | Detalha a saída da base e o clique com redstone num Cubit desligado. |
| Marcelo Fabiano | 2026-11-05 16:57 | Adota o formato e os estados da prancha de conceito. |
| Marcelo Fabiano | 2026-11-05 16:09 | Detalha o Dock: formato, dono, tela, quebra, teleporte e o que é salvo. |
| Marcelo Fabiano | 2026-11-05 15:32 | Inclui os blocos que ferem na condição negativa. |
| Marcelo Fabiano | 2026-11-05 14:40 | Define o consumo por situação. |
| Marcelo Fabiano | 2026-11-05 13:57 | Define que as barras só ficam vazias com 0 FE. |
| Marcelo Fabiano | 2026-11-05 12:52 | Define o consumo e a reserva do slot de energia. |
| Marcelo Fabiano | 2026-11-05 12:08 | Aponta a hibernação fora da base para a Fase 5. |
| Marcelo Fabiano | 2026-11-05 02:00 | Amplia a recarga pela grade de craft para a bateria abaixo da carga de religar. |
| Marcelo Fabiano | 2026-11-05 01:58 | Detalha o uso da recarga pela grade de craft. |
| Marcelo Fabiano | 2026-11-05 01:57 | Adiciona a recarga do item pela grade de craft. |
| Marcelo Fabiano | 2026-11-05 00:12 | Cita os sons de ligar, desligar e recarregar. |
| Marcelo Fabiano | 2026-11-04 18:50 | Define o estado ao sair da base com outro seguindo. |
| Marcelo Fabiano | 2026-11-04 17:26 | Esclarece a exceção do robô desligado pelo controle. |
| Marcelo Fabiano | 2026-11-04 17:15 | Renomeia para português e atualiza os caminhos. |
| Marcelo Fabiano | 2026-11-04 17:12 | Adiciona a espera na frente da base ocupada. |
| Marcelo Fabiano | 2026-11-04 17:11 | Atualiza a chamada ao Dock e o comportamento ao sair da base. |
| Marcelo Fabiano | 2026-11-04 17:08 | Adiciona a hibernação sem bateria, o ligar pelo clique e as regras da redstone. |
| Marcelo Fabiano | 2026-11-04 16:57 | Define os estados, as formas de recarga, o painel solar, o Dock e o slot de energia. |
| Marcelo Fabiano | 2026-11-04 15:23 | Descreve as features do Cubit. |
