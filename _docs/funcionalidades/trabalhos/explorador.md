# Explorador

Anotação da ideia de uma profissão nova, o Explorador (perguntas 1668 a 1671). **Nada aqui está decidido.** É o registro da ideia do desenvolvedor, para não se perder, e a lista do que vai ao refinamento quando o card começar (regras 11, 50 e 53 do `AGENTS.md`). Ele complementa [`trabalhos.md`](../trabalhos.md).

**Situação:** ideia. Entra depois do Mecânico, na ordem das profissões (pergunta 1669).

## A ideia do desenvolvedor

1. **App novo:** com o card do Explorador no Cubit escolhido, o player ganha um app novo no Tablet.
2. **Lista de biomas:** o app lista os biomas da dimensão onde o player está. No Nether, a lista é do Nether, e no End, do End. A lista pode incluir o portal do End.
3. **Guia:** o player escolhe um bioma da lista, e o Cubit o guia até entrar nele.
4. **Marcações:** o Explorador cria pontos de marcação onde está, com o nome escolhido pelo player e uma cor entre as do jogo.
5. **Localizar:** na lista, o player localiza biomas ou marcações, com um filtro.
6. **Avisos:** o Explorador avisa quando começa o trajeto e quando está perto do destino.
7. **Mapas:** enquanto anda guiando o player, ele gera mapas do jogo da região, aproveitando cada mapa da melhor forma. Nunca há mapa sobre mapa: cada mapa criado complementa os anteriores, sem repetir área. Isso só acontece quando há mapas em branco disponíveis no inventário (pergunta 1671).

## Em aberto, para o refinamento

1. **Card:** o nome no jogo e o ID, como `Machine Learning: Explorer` e `cubit:ml_explorer`, o tier mínimo, a receita e o item do trabalho na receita.
2. **Nome do app:** o nome, a cor e a regra de quando aparece, só com o card no Cubit escolhido, como o app Escort (pergunta 1664).
3. **Biomas da lista:** todos os biomas que a dimensão pode gerar, inclusive os de outros mods, sem lista fixa (regra 49), e como mostrar um bioma que não existe perto.
4. **Busca:** o raio da busca por tier, o custo em FE e o tempo da busca, sem travar o servidor (regra 21) nem carregar chunks à força (regra 22).
5. **Portal do End:** como localizar a fortaleza, que é uma estrutura e não um bioma, e se outras estruturas entram, como a fortaleza do Nether.
6. **Guia:** se o Cubit anda à frente do player, se mostra uma seta ou uma linha, a distância que mantém, e o que acontece com obstáculos, água, Nether e mobs no caminho.
7. **Chegada:** o que conta como "entrar no bioma" e o que acontece quando chega.
8. **Marcações:** o limite por tier, onde ficam salvas (no Cubit, no Tablet ou no player), se valem para todas as dimensões, se podem ser renomeadas ou apagadas, e como aparecem no mundo.
9. **Cores:** quais cores do jogo, como as 16 dos corantes, e como o player escolhe.
10. **Filtro:** por nome, por tipo (bioma ou marcação) e por distância.
11. **Avisos:** os textos, a distância de "perto", os sons e as falas da profissão, como nas outras (perguntas 1583 a 1590).
12. **Visual:** o chapéu, a mochila e a ferramenta na garra, como nos outros cards (regra 9).
13. **Sem magia:** a busca precisa de uma explicação tecnológica, como um mapa de satélite ou um radar, e nunca mágica (regra 2).
14. **Mapas, de onde vêm:** em qual inventário ficam os mapas em branco, se no do Cubit, no do player ou nos dois, e para onde vão os mapas prontos.
15. **Mapas, a grade:** no jogo, um mapa cobre um quadrado fixo de uma grade (128 blocos de lado na escala 1:1, e o dobro a cada escala). Falta definir a escala, ou se ela depende do tier, e como o Explorador escolhe o próximo quadrado ainda sem mapa, para cobrir o trajeto sem sobrepor.
16. **Mapas, a memória:** como ele sabe quais quadrados já têm mapa, contando os mapas que ele já fez, os do player e os de outros Cubits, e em que dimensões.
17. **Mapas, o preenchimento:** um mapa do jogo só se desenha onde alguém passa. Falta definir se o Explorador desenha o mapa enquanto anda, como o player segurando o mapa, e se os mapas gastam FE.
18. **Contrato:** o card, a receita, o app, o NBT ou o attachment das marcações e dos mapas, os pacotes e as chaves de config.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-22 13:40 | Acrescenta os mapas complementares à ideia (pergunta 1671). |
| Zennd | 2026-11-22 13:30 | Anota a ideia do Explorador, sem nada decidido (perguntas 1668 a 1670). |
