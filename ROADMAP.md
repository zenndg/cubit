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
3. Os trabalhos, um card cada: Pescador, Fazendeiro, Organizador, Cozinheiro, Patrulheiro Galático, Lenhador, Pecuarista, Mecânico, Trader, Explorador, Alquimista, Minerador e Construtor. **Entregues:** Pescador, Fazendeiro, Organizador, Cozinheiro e Patrulheiro Galático. Depois do Patrulheiro Galático, as próximas profissões são o Lenhador, uma profissão nova, o Pecuarista, o Mecânico, o Trader, que negocia com os aldeões, o Explorador, outra profissão nova, e o Alquimista, nesta ordem, e depois o Minerador e o Construtor (perguntas 1594, 1666, 1668, 1669 e 1927). Toda profissão entra com o seu visual, no próprio card, sem um card só de personalização (pergunta 1930). Antes do Patrulheiro Galático entraram as receitas novas, as peças de montagem, as portas, os tiers do Dock, o Dock Solar e a Recharge Splash (perguntas 1338, 1359, 1383 e 1393) e, com prioridade sobre o resto do roadmap, a Engineering Desk, a mesa de criação própria do mod, de onde saem todos os itens dele, a revisão dos trabalhos por tier e a revisão das receitas dos cards. O Patrulheiro Galático substitui o Defensor (perguntas 1390, 1424 e 1550). Logo depois dele entraram o Dock unificado, à frente do XP (pergunta 1852), e a luz da lua no Dock Ultimate (perguntas 1941 a 1963). Depois da auditoria dos crafts e da fabricação em cascata, dos itens 4 e 5, entram o XP Cubit e Dock, o XP em tanque de líquido, as peças e o crafting, e o Servidor e as Antenas, com a energia sem fio e o carregamento de chunks, nesta ordem, antes das outras profissões (perguntas 1777 a 1779, 1787 a 1789 e 2118). Entre o Fazendeiro e o Organizador entrou o controle de velocidade do Cubit, com − e +, que vale para todo Cubit (perguntas 1261 e 1263).
4. **CRÍTICO, entregue em parte:** árvore de crafts, com o que cada um libera, e auditoria do progresso do player do início ao fim, com a avaliação dos itens bloqueados na Engineering Desk que impediriam o player de começar. Entra logo depois da luz da lua no Dock e antes do XP Cubit e Dock, na issue #250 (perguntas 1951 e 1965 a 1977). Entraram o guia do player com a árvore de crafts e o pedido na mesa, com a liberação protegida (perguntas 1982 a 1995). Faltam as conquistas visíveis, que ficaram para outro dia (pergunta 2005). O bloqueio visto num mundo novo não se repetiu e ficou como falso positivo naquele cenário (pergunta 2002).
5. **Entregue:** Engineering Desk, com a fabricação em cascata, exclusiva do último tier, em dois PRs, com o conceito em [`cascata-da-mesa.md`](_docs/funcionalidades/cascata-da-mesa.md) na issue #253 (perguntas 1964 e 2006 a 2056).
6. **Entregue:** XP Cubit e Dock, o XP do Cubit num tanque de líquido, passado ao tanque do Dock, de onde o player o puxa, na issue #244, em dois PRs: o Cubit, no #257, e o Dock, no #258 (perguntas 1762, 1787 a 1789, 1814 a 1851 e 2060 a 2130).
7. **Entregue:** peças e crafting, com os itens do mod montados com peças compartilhadas, cada uma com uma função que vários itens têm em comum, na issue #259, com o conceito em [`pecas.md`](_docs/funcionalidades/pecas.md) (perguntas 2118 e 2131 a 2180).
8. Servidor e Antenas, com a energia sem fio, o carregamento de chunks, o log e a viagem até o Dock, depois das peças (perguntas 1777 a 1779, 1788 e 2118). A energia sem fio ainda vai ao refinamento.
9. Storage wireless.

## Ainda sem fase

- Parar pelo Tablet (`Stop`) e seguir fora do modo área, com um refinamento de como o Cubit parado convive com a busca, o choque e o Dock (pergunta 1036). O `Follow` entra com o retorno automático, no #181, e tira o Cubit da área com o item 13 (perguntas 1055 e 1072). O `Stop` fica para depois de concluir a Fase 4.2 (pergunta 1076).
- Trabalho do Arquiteto, que copia uma estrutura de uma área para outra (pergunta 1146). O Comerciante, que negocia com aldeões, virou o Trader e entrou na fila depois do Mecânico (pergunta 1927).
- Wiki e livro para o player.
