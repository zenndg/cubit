# Roadmap

As fases do Cubit, do conceito aos trabalhos. O que está pronto em cada uma é marcado no [`checklist.md`](checklist.md), e as decisões ficam em [`_docs/perguntas/`](_docs/perguntas/README.md), que prevalecem sobre este arquivo. Os números de cada recurso ficam em [`_docs/tiers.md`](_docs/tiers.md).

Cada fase só começa com pedido explícito, e cada funcionalidade entra por issue, documentada em [`_docs/funcionalidades/`](_docs/funcionalidades/) antes do código.

## Fase 1: Conceito (encerrada)

Persona, visual, expressões, funcionalidades, itens, tiers, glossário, regras, pilares e fluxo de trabalho, em documentos.

## Fase 2: Versão mínima (encerrada)

- Crafting do Cubit, do Induction Dock e da Toolbox, com materiais do jogo puro.
- Seguir o player, com teleporte e parada, e vários Cubits com um seguindo.
- Inventário, bateria com consumo por situação e recarga com redstone.
- Induction Dock, com hibernação e fila.
- Reações a fogo, lava e blocos que ferem.
- Virar item, morte com o Damaged Cubit e conserto.
- O comando `/cubit reset` e o modelo novo.

## Fase 3: Sentidos (encerrada)

Todas as funcionalidades entraram:

- painel solar e lanterna;
- scanners de minérios e de mobs, com a caixa, a seta e as cores das antenas;
- os sons, os textos curtos e as falas;
- as animações, as expressões, o nome e a cor da carcaça;
- o Jade e as telas próprias do Cubit e do Induction Dock.

### Primeira versão: `0.1.0`

Saiu ao terminar a Fase 3, com tag e release no GitHub, com o mod funcionando só com ele e as dependências: GeckoLib e LambDynamicLights, obrigatórias, e Jade, opcional. O envio ao Modrinth e ao CurseForge está pendente, nesta versão e na `0.1.1` (perguntas 2171 e 2186).

## Fase 4: Upgrades e autonomia (encerrada)

Dividida em três partes, na ordem em que uma depende da outra.

### Fase 4: Tier e ataque (encerrada)

- Tiers Advanced, Elite e Ultimate, com os módulos de upgrade.
- Choque, com o modo Pacífico, o único do tier Base.
- Modos de defesa Contra-atacar e Agressivo.
- Modo de defesa só o alvo, com o último mob atacado.
- Interface de configuração na aba Settings, com a chave geral e a do painel solar.

### Fase 4.1: Itens, filtros, carregamento e mais (encerrada)

- Ímã e coleta de itens do chão, com o filtro em lista branca e lista negra.
- Power Bank, também no Dock e na grade de craft.
- Inventário do Induction Dock, numa aba nova, para a Toolbox e as Power Banks, que ele carrega.
- Slot exclusivo de tochas, do Advanced em diante, com o Cubit colocando tochas sozinho.

### Fase 4.2: Tablet, área e retorno (encerrada)

- Cubit Tablet, que substitui o Cubit Remote: tela de seleção e tela do Cubit, com os apps Open, Alvo e Dock.
- Modo área e descarga em baú marcado, com os apps Área e Descarga do Tablet e o botão Descarregar.
- Retorno automático e local de morte, com as linhas deles na aba Recursos do Tablet.

## Fase 5: Trabalhos (em andamento)

Entregue como um kanban, no GitHub Project "Cubit · Fase 5", um card por vez, e cada card é uma issue e um PR (perguntas 1137, 1138 e 1156). A ordem segue a pergunta 251, com a base e os trabalhos primeiro e a infraestrutura no fim.

1. **Entregue:** consolidar o conceito da Fase 5.
2. **Entregue:** base do trabalho, com o slot do card, o tier mínimo, o estado de trabalho, a aba Trabalho e a hibernação fora do Dock.
3. Os trabalhos, um card cada: Pescador, Fazendeiro, Organizador, Cozinheiro, Patrulheiro Galático, Lenhador, Pecuarista, Mecânico, Trader, Explorador, Alquimista, Minerador e Construtor. **Entregues:** Pescador, Fazendeiro, Organizador, Cozinheiro e Patrulheiro Galático. Depois do Patrulheiro Galático, as próximas profissões são o Lenhador, uma profissão nova, o Pecuarista, o Mecânico, o Trader, que negocia com os aldeões, o Explorador, outra profissão nova, e o Alquimista, nesta ordem, e depois o Minerador e o Construtor (perguntas 1594, 1666, 1668, 1669 e 1927). Toda profissão entra com o seu visual, no próprio card, sem um card só de personalização (pergunta 1930). Antes do Patrulheiro Galático entraram as receitas novas, as peças de montagem, as portas, os tiers do Dock, o Dock Solar e a Recharge Splash (perguntas 1338, 1359, 1383 e 1393) e, com prioridade sobre o resto do roadmap, a Engineering Desk, a mesa de criação própria do mod, de onde saem todos os itens dele, a revisão dos trabalhos por tier e a revisão das receitas dos cards. O Patrulheiro Galático substitui o Defensor (perguntas 1390, 1424 e 1550). Logo depois dele entraram o Dock unificado, à frente do XP (pergunta 1852), e a luz da lua no Dock Ultimate (perguntas 1941 a 1963). Depois da auditoria dos crafts e da fabricação em cascata, dos itens 4 e 5, entram o XP Cubit e Dock, o XP em tanque de líquido, as peças e o crafting, o Servidor e as Antenas, com a energia sem fio e o carregamento de chunks, e o robô de transporte, nesta ordem, antes das outras profissões (perguntas 1777 a 1779, 1787 a 1789, 2118 e 2197). Entre o Fazendeiro e o Organizador entrou o controle de velocidade do Cubit, com − e +, que vale para todo Cubit (perguntas 1261 e 1263).
4. **CRÍTICO, entregue em parte:** árvore de crafts, com o que cada um libera, e auditoria do progresso do player do início ao fim, com a avaliação dos itens bloqueados na Engineering Desk que impediriam o player de começar. Entra logo depois da luz da lua no Dock e antes do XP Cubit e Dock, na issue #250 (perguntas 1951 e 1965 a 1977). Entraram o guia do player com a árvore de crafts e o pedido na mesa, com a liberação protegida (perguntas 1982 a 1995). Faltam as conquistas visíveis, que ficaram para outro dia (pergunta 2005). O bloqueio visto num mundo novo não se repetiu e ficou como falso positivo naquele cenário (pergunta 2002).
5. **Entregue:** Engineering Desk, com a fabricação em cascata, exclusiva do último tier, em dois PRs, com o conceito em [`cascata-da-mesa.md`](_docs/funcionalidades/cascata-da-mesa.md) na issue #253 (perguntas 1964 e 2006 a 2056).
6. **Entregue:** XP Cubit e Dock, o XP do Cubit num tanque de líquido, passado ao tanque do Dock, de onde o player o puxa, na issue #244, em dois PRs: o Cubit, no #257, e o Dock, no #258 (perguntas 1762, 1787 a 1789, 1814 a 1851 e 2060 a 2130).
7. **Entregue:** peças e crafting, com os itens do mod montados com peças compartilhadas, cada uma com uma função que vários itens têm em comum, na issue #259, com o conceito em [`pecas.md`](_docs/funcionalidades/pecas.md) (perguntas 2118 e 2131 a 2180).
8. Network, a rede sem fio: o Servidor, o Solar Relay e a antena, com a energia, o carregamento de chunks, o log e o XP, depois das peças (perguntas 1777 a 1779, 1788, 2118 e 2196 a 2340). As 9 entregas estão em [Network](#network-a-rede-sem-fio-em-conceito).
9. Robô de transporte, um segundo robô, em forma de caminhão, que só faz a logística: traz os recursos de cada farm para a base. Entra logo depois da Network, o Servidor e as Antenas, completa, e antes das outras profissões. A ideia está no [`backlog.md`](_docs/backlog.md#robô-de-transporte) e ainda vai ao refinamento (pergunta 2197).
10. Storage wireless.

## Network: a rede sem fio (em conceito)

O Network Server, o Solar Relay e a Signal Antenna levam energia até os Cubits longe da base, trazem o XP de volta e mantêm os chunks de trabalho carregados. É o item 8 da Fase 5 (pergunta 2196).

Cada entrega é um PR para a branch `integration/network`, e só a feature completa, testada e aprovada vai à `develop`, num PR final (perguntas 2203 a 2205, 2288 e 2318). Nenhum código começa sem a autorização do desenvolvedor, com o conceito fechado (pergunta 2224). A versão é a `0.2.0` (pergunta 2314).

Em toda entrega:

- [ ] recursos aceitos antes do código (regra 59) e prévia aprovada de cada tela;
- [ ] inglês e português;
- [ ] no fim, nesta ordem: commits, GameTests sobre o último commit, teste manual do desenvolvedor e só então o PR, com as provas do `antes-do-pr.md` (pergunta 2293).

### Entrega 1: Conceito

- [x] `_docs/funcionalidades/network.md`, com as decisões, os detalhes técnicos e o contrato, já escrito e autorizado (pergunta 2325)
- [x] números em `tiers.md`, com cada chave no `config.md` na entrega que a lê (pergunta 2349), e os nomes em `itens.md` e `glossario.md`
- [x] `modo-area.md`, `tablet.md`, `energia.md`, `xp.md`, `telas.md`, `trabalhos.md` e os cards, conferidos com as mudanças
- [x] roteiro manual de cada entrega e a mudança do Gitflow no `AGENTS.md` e no `fluxo-de-trabalho.md`
- **Recursos:** pranchas do Servidor, do Solar Relay e da antena, refeitas
- **GameTests:** nenhuma

### Entrega 2: Área desenhada

- [ ] o app Área por pontos, em ângulo reto, de 4 a 32, a até 32 blocos, com a linha de 3 px, vermelha e verde ao fechar
- [ ] Shift + clique desfaz o último ponto ou aplica, e apaga a área, com confirmação
- [ ] a altura do ponto mais baixo ao mais alto, até o limite do tier
- [ ] todos os trabalhos, a ronda e a volta à área com a forma nova, e a área antiga apagada
- [ ] o Organizador e o Cozinheiro no Advanced, com a receita liberada pelo Advanced Module
- **Recursos:** nenhum, só prévia da linha
- **GameTests:** formas, bordas, recusas, L, altura e cada trabalho dentro da forma

### Entrega 3: Network Server e Solar Relay

- [ ] Transceptor, Âncora de Chunk e as receitas dos dois blocos, na categoria `Dock and Network`
- [ ] modelos, texturas, a frente com os 16 quadradinhos, ainda sem canais, e as 3 colunas, e as luzes
- [ ] par no mesmo chunk, geração de 500 a 4.000 FE/t, com a noite só no Ultimate
- [ ] bateria e tanque 4× o Dock, entrada por cabo e redstone, e upgrade pelos Modules
- [ ] chave e UPS do chunk do Servidor, e inventário de 27 a 54 slots de 128, com as regras do Dock
- [ ] as telas do Servidor, nas abas Energy e Storage, e do Solar Relay, com Rename
- [ ] os modelos acima do Dock, com as animações por estado (perguntas 2336 a 2338)
- **Recursos:** 2 texturas do Servidor, 1 do Relay, 2 peças, ícones das abas e 9 sons
- **GameTests:** par, recusas, geração, tiers, quebrar e recolocar, chunk e UPS, slots de 128

### Entrega 4: Signal Antenna e chunks

- [ ] o item, a receita, o modelo, as 16 cores com o fio de contorno e a tela, com as três chaves
- [ ] a ligação ao Servidor, o canal escolhido pelo player, a ordem fixa em inglês e 4, 8, 12 e 16 por tier
- [ ] um de cada bloco por chunk, de qualquer dono, e a reconexão ao mover o Servidor ou a antena
- [ ] a energia até a antena e o chunk carregado, com 25 FE/t, o custo da distância, o UPS e a retomada uma a uma
- [ ] os limites de 48 por player e 192 no mundo, o dono offline e o `/cubit network`
- [ ] a parede do chunk, vista só pelo dono com o Tablet no app Network
- [ ] o modelo acima do Dock, com as animações do sinal (perguntas 2336 a 2338)
- **Recursos:** 1 textura da antena, 1 ícone de item e 5 sons
- **GameTests:** ligar, canais, limites, queda e volta de cada elo, dono offline, soma de FE

### Entrega 5: App Network, ligações e energia

- [ ] a rede de teste das GameTests, usada por todas as de área
- [ ] o app Network: antena, Cubit, Docks e baús, com as setas, as confirmações e o desfazer
- [ ] 1, 4 e 9 antenas por Cubit, em chunks vizinhos, e a área desenhada dentro deles
- [ ] 1, 2, 4 e 6 baús e Docks por tier, e 12, 18 e 25 baús do Organizador, de qualquer storage, a qualquer distância nos chunks
- [ ] a energia até o Dock, com a divisão por igual
- **Recursos:** 1 ícone do app
- **GameTests:** ligações, recusas, vizinhança, energia curta e soma de FE

### Entrega 6: A rede obrigatória e os trabalhos

- [ ] todo o modo área só com a rede, com a hibernação `No network` e `Owner offline`, o `Waiting for area` e o `Back to work`
- [ ] a borda dos chunks e a fuga, sem o Cubit sair
- [ ] saem o raio de chamada da área e as chaves `area_size_blocks` e `patroller_area_size` (perguntas 2350 e 2351)
- [ ] o `Follow` que pausa, recolher e morrer que apagam, e o Call e o Ir para o Dock só nos marcados
- [ ] o Patrulheiro, a escolta e o resto dos trabalhos na rede
- [ ] a aba Network na tela do Cubit e no Tablet, com os estados novos
- **Recursos:** 1 ícone da aba
- **GameTests:** hibernar e voltar com e sem card, borda, morte, escolta, cada trabalho com a rede

### Entrega 7: XP de volta

- [ ] os dois modos novos do Dock e a subida pela antena e pelo Solar Relay
- [ ] o tanque do Servidor, com os três modos, a troca com o player e os canos
- [ ] a aba XP do Servidor e os tanques da antena e do Solar Relay
- **Recursos:** os da aba XP, se a prévia pedir
- **GameTests:** subida, espera, tanque cheio, modos e soma de XP e de FE

### Entrega 8: Painel de controle

- [ ] a aba Network do Servidor, com a árvore de antenas, Docks, baús e Cubits
- [ ] o Log, com 100 eventos, e os avisos na barra de ação
- [ ] os estados da frente do Servidor, com os números
- **Recursos:** ícones dos estados e da árvore
- **GameTests:** estados, prioridade, eventos e avisos uma vez

### Entrega 9: Acabamento

- [ ] o Jade, as dicas e os sons que faltarem
- [ ] o guia do player, a árvore de crafts e a imagem dela
- [ ] o teste de desempenho com 20 a 30 Cubits em 16 antenas, com o tempo de tick, e os quadros por segundo ao lado de 20 blocos da rede (pergunta 2346)
- [ ] o teste na instância só com o mod e no Iron Meridian, e a revisão final da feature
- **Recursos:** a imagem da árvore
- **GameTests:** a suíte inteira, com a feature completa

## Ainda sem fase

- Parar pelo Tablet (`Stop`) e seguir fora do modo área, com um refinamento de como o Cubit parado convive com a busca, o choque e o Dock (pergunta 1036). O `Follow` entra com o retorno automático, no #181, e tira o Cubit da área com o item 13 (perguntas 1055 e 1072). O `Stop` fica para depois de concluir a Fase 4.2 (pergunta 1076).
- Trabalho do Arquiteto, que copia uma estrutura de uma área para outra (pergunta 1146). O Comerciante, que negocia com aldeões, virou o Trader e entrou na fila depois do Mecânico (pergunta 1927).
- Wiki e livro para o player.
