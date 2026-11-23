# XP Cubit e Dock

O XP passa a ser um fluido do mod, o **Cubit XP Fluid**, guardado num tanque do Cubit e num tanque do Induction Dock. O Cubit enche o tanque com o XP do trabalho e com as bolinhas do chão, e o Dock recebe esse XP, guarda, converte em energia ou entrega ao player. O conceito inicial é do desenvolvedor (perguntas 1762, 1787 a 1789 e 1814) e foi refinado nas perguntas 1815 a 1850. Os números ficam em [`tiers.md`](../tiers.md#xp).

**Situação:** autorizado (pergunta 1851). O PR 1 traz o Cubit, e o PR 2, o Dock.

## Por quê

O Cubit já gera XP no trabalho, mas ele se perde, ou vira energia, no caso do Patrulheiro. Com o tanque, o XP do trabalho fica guardado para o player, que o pega no Dock quando quiser, e o que sobra pode virar um pouco de energia, sem quebrar o equilíbrio do jogo com uma fazenda de mobs (pergunta 1834).

## O fluido

- **Nome e ID:** `cubit:xp_fluid`, "Cubit XP Fluid" em inglês e "Fluido de XP Cubit" em português (pergunta 1816).
- **Onde existe:** é um fluido registrado do NeoForge, verde-limão, que vive só dentro dos tanques do Cubit e do Dock e passa por canos de outros mods. Não tem balde nem bloco no mundo (pergunta 1815).
- **Taxa:** 1 ponto de XP do jogo vale 20 mB, a taxa do Industrial Foregoing e da maioria dos mods de XP líquido. Um balde, de 1.000 mB, guarda 50 pontos. A taxa fica no config (pergunta 1817).
- **Tag:** o fluido entra na tag comum `c:experience`, para os mods que a usam o reconhecerem como XP (pergunta 1818).
- **Texturas:** parado e correndo, animadas, num verde-limão com brilho suave e pequenas bolhas, com desenho próprio (regra 9), geradas por script e aprovadas pelo desenvolvedor antes de entrarem (pergunta 1845).
- **Independência:** o Industrial Foregoing é só a referência da taxa. Nada depende de outro mod, e tudo funciona pela tela (regra 50).

## O tanque do Cubit

- **Capacidade:** por tier, de 8 a 64 baldes, em [`tiers.md`](../tiers.md#xp) (pergunta 1826).
- **Quando enche:** só com o Cubit ativo, como na coleta de itens. Desligado ou hibernando, no Dock ou fora dele, ele não recolhe bolinhas nem recebe XP. No Dock, o tanque só esvazia (pergunta 1844).
- **Guardar:** o tanque, o modo e a chave do excesso ficam no NBT do Cubit e, ao recolher, no estado do item. O Damaged Cubit também os guarda, sem perder nem duplicar, e ao subir de tier com um Module o XP continua (pergunta 1838).
- **Saves antigos:** um Cubit de um mundo de antes do XP vem com o tanque vazio, no modo Armazenar, com o "Apagar o excesso" desligado (regra 18 e pergunta 1829).

## De onde vem o XP

### XP do trabalho

O XP que o trabalho gera vai **direto para o tanque**, sem bolinha no mundo (pergunta 1819), só nos trabalhos da lista `xp_jobs` do config, que o pack pode mudar (pergunta 1820). Por padrão, a lista tem:

| Trabalho | XP | Pergunta |
|---|---|---|
| Patrulheiro Galático | O XP que o mob abatido daria, perto ou longe do dono. O dono deixa de receber o XP dos abates e o pega no Dock | 1821 |
| Pescador | De 1 a 6 pontos por pesca, como a vara do jogo dá ao player | 1820 |
| Cozinheiro | O XP guardado na fornalha, ao tirar os itens prontos, com o arredondamento sorteado do jogo. A fornalha o zera, para o XP não sair duas vezes | 1824 |
| Fazendeiro | 20% de chance de 1 ponto por planta madura colhida, no config, porque o jogo puro não dá XP por colheita | 1823 |

O Organizador não gera XP. Um trabalho fora da lista age como antes do XP Cubit: o abate do Patrulheiro solta as bolinhas normais do jogo, o XP da fornalha fica nela para o player, e o Pescador e o Fazendeiro não geram XP (pergunta 1839).

### Bolinhas do chão

As bolinhas de XP de qualquer origem entram no tanque como os itens, com as chaves de coleta e de ímã da [aba Behavior](telas.md#aba-behavior) (pergunta 1822):

- **Toque:** desde o Base, o Cubit pega a bolinha que encosta nele, com a mesma caixa da coleta de itens.
- **Ímã:** do Advanced em diante, puxa as bolinhas no raio do tier, com o mesmo custo por bolinha que tem por item.
- **Coleta desligada:** nenhuma bolinha é recolhida, em nenhum modo.
- **Disputa com o player:** o Cubit pega todas as bolinhas ao alcance, mesmo a que voa para o player. O player perto do Cubit perde parte do XP para o tanque e o pega depois no Dock (pergunta 1843).

### Patrulheiro

O XP dos abates deixa de virar energia na bateria (perguntas 1728 a 1730, mudadas pela 1821). O `PatrolXp` e as chaves `patroller_xp_energy` e `patroller_xp_owner_distance`, que nunca foram publicadas, saem sem migração (pergunta 1825).

## Modos do Cubit

Escolhidos na aba XP (pergunta 1827):

| Modo | XP do trabalho | Bolinhas do chão | Tanque cheio |
|---|---|---|---|
| Ignorar | Apagado | Recolhidas e apagadas, ao toque e pelo ímã | Não enche |
| Armazenar | No tanque | No tanque | Com "Apagar o excesso" ligado, o que não cabe some. Desligado, ele para de recolher bolinhas, e o XP do trabalho que não cabe cai como bolinha ao lado dele, sem perder nada |
| Voltar ao Dock | No tanque | No tanque | Vai ao Dock, esvazia o tanque e volta ao trabalho |

### Voltar ao Dock

- **Quando:** com o tanque 100% cheio.
- **Qual Dock:** o Dock preferido do Tablet ou o mais perto, como o retorno automático da bateria (pergunta 1828).
- **Sem Dock alcançável:** avisa o dono uma vez e age como Armazenar, sem apagar o excesso, até o tanque ter espaço (pergunta 1828).
- **Saída:** sai do Dock com o tanque vazio e, como hoje, com a bateria e a vida cheias, e volta ao trabalho. Uma parada resolve as duas coisas (pergunta 1842).
- **Dock cheio:** pela pergunta 1831, o Dock sempre aceita o XP, porque apaga ou converte o excesso, então o Cubit nunca fica com XP por falta de espaço no Dock.

## O tanque do Dock

- **Capacidade:** por tier, 4 vezes a do Cubit, de 32 a 256 baldes, em [`tiers.md`](../tiers.md#xp) (pergunta 1833).
- **Passagem:** sempre que o Cubit está no Dock, por qualquer motivo, o XP passa do tanque dele para o do Dock aos poucos, com a velocidade do tier do Dock, na mesma proporção da recarga. A tela mostra a passagem (pergunta 1835).
- **Canos:** o tanque do Dock aceita e entrega o fluido pela capability de fluido do NeoForge. Entra só o Cubit XP Fluid, e não os fluidos de XP de outros mods, para nenhuma taxa diferente criar ou perder XP. Sai só o Cubit XP Fluid (perguntas 1818 e 1841). O tanque do Cubit não tem cano.
- **Quebrar:** o item do Dock guarda o XP e o modo, como já guarda a energia e o inventário (pergunta 1838).
- **Saves antigos:** um Dock de um mundo de antes do XP vem com o tanque vazio, no modo Armazenar e converter o excesso (pergunta 1832).

## Modos do Dock

Escolhidos na aba XP do Dock (pergunta 1831):

| Modo | O que faz com o XP recebido |
|---|---|
| Apagar tudo | Apaga |
| Armazenar e apagar o excesso | Guarda e, cheio, apaga o que não cabe |
| Armazenar e converter o excesso | Guarda e, cheio, converte o que não cabe em energia. É o padrão (pergunta 1832) |
| Converter tudo | Converte em energia |
| Guardar metade e converter o resto | Guarda até 50% do tanque, no config, e converte o que passar |

- **Conversão:** 2 pontos de XP (40 mB) viram 1 FE na bateria do Dock, na chave `dock_xp_points_per_fe`. A sobra que não fecha 1 FE fica no tanque (pergunta 1834).
- **Bateria cheia:** nos modos que convertem, com a bateria do Dock cheia, o XP que iria virar energia é apagado. Assim o Dock sempre aceita o XP do Cubit (pergunta 1831).
- **Equilíbrio:** uma fazenda de mobs de 3.000 pontos por hora rende 1.500 FE por hora, cerca de 2% do painel solar do Dock Base. O XP é um uso para o que sobra, nunca uma fonte de energia (pergunta 1834).

## Entregar ao player

Um botão na aba XP do Dock (pergunta 1836):

- **Clique:** entrega os pontos que faltam para o player subir 1 nível, pela conta do jogo, ou o que houver no tanque, se for menos.
- **Shift + clique:** entrega tudo.
- **Como:** os pontos entram direto no player, sem bolinhas, e só pontos inteiros saem. O que não fecha 1 ponto fica no tanque.
- **Quem:** só o dono, com o servidor conferindo o dono e a distância (regra 20).

## Telas

- **Aba XP do Cubit:** nas abas da tela do Cubit, com um ícone de 16×16 feito por prompt (pergunta 900). Mostra o recipiente com marcas de medida e o líquido verde-limão, a linha `6,400 / 8,000 mB · 320 points`, o modo e a chave "Apagar o excesso" (perguntas 1830 e 1847).
- **Aba XP no Tablet:** a mesma aba na tela do Cubit no Tablet. Com o Cubit descarregado, ela mostra a última foto dele, só para ler (perguntas 1830 e 1849).
- **Aba XP do Dock:** a quarta aba da tela do Dock, depois de Dock, Storage e Solar (pergunta 1865), com o mesmo recipiente, o modo, o botão do player e a energia que o XP gerou (pergunta 1837).
- **Botões:** pedem ao servidor, que confere o dono e decide (regras 19 e 20).
- **Dica dos itens:** o item do Cubit, o Damaged Cubit e o item do Dock ganham a linha `XP 6,400 / 8,000 mB`, como a da bateria (pergunta 1849).
- **Jade:** opcional, ganha a linha do tanque no Cubit e no Dock, como a da energia (pergunta 1837).
- **Modelos:** nada muda no modelo do Cubit nem no do Dock (pergunta 1849).

## Avisos

Um texto curto, uma vez por enchida, que só volta depois que o tanque esvaziar um pouco, em inglês e português (pergunta 1840):

- **Cubit cheio,** no Armazenar sem apagar o excesso ou no Voltar ao Dock sem Dock: o Cubit fala na barra de ação do dono, com o bipe, como as outras falas, por exemplo `Cubit: my XP tank is full`.
- **Dock cheio,** apagando ou convertendo o excesso: o dono por perto lê `The Dock XP tank is full`.

## Sons

Os do jogo, sem `.wav` novos (pergunta 1848):

- **Recolher bolinha:** o som de pegar XP do jogo, um pouco mais grave, no Cubit.
- **Entregar ao player:** o mesmo som, no player.
- **Passagem ao Dock:** em silêncio, porque a recarga já toca.

## Fora do escopo

- **Balde e bloco do fluido:** o fluido não sai dos tanques (pergunta 1815).
- **XP de outros mods:** o Dock não aceita os fluidos de XP de outros mods (pergunta 1841).
- **Organizador:** não gera XP (pergunta 1820).

## Contrato

| Onde | Nome | Conteúdo |
|---|---|---|
| Fluido | `cubit:xp_fluid` | O Cubit XP Fluid, sem balde e sem bloco (perguntas 1815 e 1816) |
| Tag de fluido | `c:experience` | Recebe `cubit:xp_fluid` (pergunta 1818) |
| Texturas | `textures/block/xp_fluid_still.png` e `xp_fluid_flow.png` | O fluido parado e correndo, animados (pergunta 1845) |
| Config, geral | `xp_fluid_per_point`, `xp_jobs`, `farmer_xp_chance`, `dock_xp_points_per_fe` e `dock_xp_storage_percent` | 20 mB, `patroller`, `fisher`, `cook` e `farmer`, 20%, 2 pontos e 50% (perguntas 1817, 1820, 1823, 1831 e 1834) |
| Config, por tier | `xp_tank_capacity`, `dock_xp_tank_capacity` e `dock_xp_transfer_per_tick` | Os números de [`tiers.md`](../tiers.md#xp) (perguntas 1826, 1833 e 1835) |
| Config, removidas | `patroller_xp_energy` e `patroller_xp_owner_distance` | Saem sem migração, porque nunca foram publicadas (pergunta 1825) |
| NBT do Cubit | `XpTank`, `XpMode` e `XpDeleteExcess` | O fluido em mB, o modo (`ignore`, `store` ou `return_to_dock`) e a chave do excesso. Sem as chaves, tanque vazio, `store` e desligada (pergunta 1829) |
| Estado do item do Cubit | Os mesmos três campos | No data component do estado do Cubit, opcionais, com os mesmos padrões, também no Damaged Cubit (regra 8 e pergunta 1838) |
| NBT do Dock | `XpTank`, `XpMode` e `XpEnergyMade` | O fluido em mB, o modo (`delete_all`, `store_delete_excess`, `store_convert_excess`, `convert_all` ou `store_half_convert_rest`) e a energia que o XP gerou. Sem as chaves, tanque vazio e `store_convert_excess` (pergunta 1832) |
| Estado do item do Dock | Os mesmos três campos | No item do Dock, como a energia (pergunta 1838) |
| Capability do Dock | Fluido do NeoForge | Entrada e saída só de `cubit:xp_fluid` (perguntas 1818 e 1841) |
| Traduções | O fluido, as abas, os modos, a chave do excesso, o botão, as dicas e os dois avisos | Em inglês e português, com as mesmas chaves (regra 1) |

## Entrega

Uma issue e dois PRs, cada um com o seu teste manual e as provas do [`antes-do-pr.md`](../desenvolvimento/antes-do-pr.md) (perguntas 1846 e 1847):

1. **PR 1, o Cubit:** o fluido e as texturas, o tanque do Cubit, as fontes de XP, os três modos, a saída do XP como energia do Patrulheiro, a passagem para um tanque do Dock ainda sem tela e sem modos, que só guarda, com o Cubit ficando com o que não couber, a aba XP da tela do Cubit, com o ícone, a dica do item, o aviso do Cubit cheio e os sons.
2. **PR 2, o Dock:** os cinco modos do Dock, a conversão em energia, o botão do player, os canos, a aba XP do Tablet e do Dock, a dica do item do Dock, os avisos do Dock e o Jade.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-22 23:45 | Tira o Dock Solar, que virou o painel de todo Dock, e põe a aba XP depois da Solar (perguntas 1854 e 1865). |
| Zennd | 2026-11-22 23:10 | Cria o conceito do XP Cubit e Dock (perguntas 1762, 1787 a 1789 e 1814 a 1850). |
