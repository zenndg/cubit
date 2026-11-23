# Árvore de crafts

Este é o guia do player: o que fabricar primeiro, o que cada item libera e o que fazer quando algo aparece bloqueado. Os nomes estão em português, com o nome em inglês ao lado, porque o jogo mostra um ou outro conforme o idioma escolhido.

## Comece por aqui

1. **Faça a Mesa de Engenharia** na mesa de craft comum do jogo. É o único item do mod feito ali.
2. **Coloque a mesa** num lugar com 2 blocos livres lado a lado.
3. **Dê energia:** ponha pó ou bloco de redstone no slot de energia da mesa. Um pó de redstone paga 2 crafts.
4. **Ponha os materiais** na entrada da mesa, escolha a receita no catálogo e aperte **+** para pedir a quantidade. A mesa só fabrica depois do +.
5. **Faça as quatro peças** e, com elas, o **Cubit**.
6. **Pegue o Cubit no seu inventário.** É isso que libera a próxima etapa.

Para o primeiro Cubit, as quatro peças pedem, ao todo, 3 diamantes, 3 obsidianas, 7 lingotes de ferro, 2 pistões, 2 tochas de redstone e 4 pós de redstone. Guarde mais 3 pós de redstone para a energia da mesa. Nada disso vem do Nether.

### A receita da Mesa de Engenharia

| | Coluna 1 | Coluna 2 | Coluna 3 |
|---|---|---|---|
| Linha 1 | Painel de vidro | Redstone | Painel de vidro |
| Linha 2 | Baú | Mesa de craft | Baú |
| Linha 3 | Lingote de ferro | Lingote de ferro | Lingote de ferro |

## Como um item é liberado

- **Regra:** uma receita é liberada quando você **tem no seu inventário** o item que a libera. Basta tê-lo uma vez. Depois, a receita fica liberada para sempre, mesmo que você gaste o item.
- **Na mesa não conta:** um item pronto, parado na saída da mesa, ainda não libera nada. Tire-o de lá.
- **Vale de qualquer jeito:** o item pode ter sido feito por você, achado ou dado por um amigo.
- **Cada player libera o seu:** num servidor, o que um amigo liberou não vale para você.

## O mapa

Cada número é o da [legenda](#legenda). A seta vai do item para o que ele libera. A imagem mostra os nomes em inglês, com os cards abreviados, e o diagrama abaixo dela, os nomes em português.

![A árvore de crafts do Cubit, em pirâmide, da Mesa de Engenharia até a Power Bank Suprema](imagens/arvore-de-crafts.png)

```mermaid
flowchart TD
  n1["1 Mesa de Engenharia"] --> n2["2 Carcaça do Cubit"]
  n1 --> n3["3 Visor do Cubit"]
  n1 --> n4["4 Esteiras do Cubit"]
  n1 --> n5["5 Núcleo Neural do Cubit"]
  n1 --> n6["6 Caixa de Ferramentas"]
  n2 --> n7["7 Cubit"]
  n3 --> n7
  n4 --> n7
  n5 --> n7
  n7 --> n8["8 Base de Indução"]
  n7 --> n9["9 Tablet do Cubit"]
  n7 --> n10["10 Mistura de Recarga"]
  n7 --> n11["11 Carcaça da Power Bank"]
  n7 --> n12["12 Célula de Energia Base"]
  n7 --> n13["13 Módulo Avançado"]
  n12 --> n14["14 Power Bank Base"]
  n13 --> n15["15 Carcaça do Card"]
  n13 --> n16["16 Placa de Circuito"]
  n13 --> n17["17 Card Pescador"]
  n13 --> n18["18 Card Fazendeiro"]
  n13 --> n19["19 Módulo Elite"]
  n14 --> n20["20 Power Bank Avançada"]
  n14 --> n21["21 Célula de Energia Avançada"]
  n19 --> n22["22 Card Organizador"]
  n19 --> n23["23 Card Cozinheiro"]
  n19 --> n24["24 Card Patrulheiro Galático"]
  n19 --> n25["25 Módulo Supremo"]
  n20 --> n26["26 Power Bank Elite"]
  n20 --> n27["27 Célula de Energia Elite"]
  n26 --> n28["28 Power Bank Suprema"]
  n26 --> n29["29 Célula de Energia Suprema"]
```

## Legenda

A mesa monta a receita sozinha, então você só precisa dos materiais, sem decorar a posição deles.

### Etapa 1: o começo

| Nº | Item | Materiais | Liberado por | Para que serve |
|---|---|---|---|---|
| 1 | Mesa de Engenharia (Engineering Desk) | A receita acima | Nada, na mesa de craft | Fabrica todo o resto do mod |
| 2 | Carcaça do Cubit (Cubit Casing) | 4 ferros, 2 obsidianas e 2 tochas de redstone | Nada, já vem liberada | Peça do Cubit |
| 3 | Visor do Cubit (Cubit Visor) | 2 diamantes e 1 obsidiana | Nada, já vem liberado | Peça do Cubit |
| 4 | Esteiras do Cubit (Cubit Treads) | 3 ferros e 2 pistões | Nada, já vêm liberadas | Peça do Cubit |
| 5 | Núcleo Neural do Cubit (Cubit Neural Core) | 1 diamante e 4 redstones | Nada, já vem liberado | Peça do Cubit e do Módulo Avançado |
| 6 | Caixa de Ferramentas (Toolbox) | 7 ferros, 1 redstone e 1 obsidiana | Nada, já vem liberada | Conserta o Cubit que morreu |
| 7 | Cubit | As peças 2, 3, 4 e 5 | Nada, já vem liberado | O seu robô companheiro |

### Etapa 2: com o Cubit

| Nº | Item | Materiais | Liberado por | Para que serve |
|---|---|---|---|---|
| 8 | Base de Indução (Induction Dock) | 1 diamante, 1 baú, 2 ferros e 1 redstone | 7 Cubit | Recarrega e cura o Cubit |
| 9 | Tablet do Cubit (Cubit Tablet) | 2 painéis de vidro, 1 redstone, 4 quartzos, 1 diamante e 1 obsidiana | 7 Cubit | Comanda os Cubits de longe |
| 10 | Mistura de Recarga (Recharge Splash) | 1 garrafa de vidro, 1 bloco de redstone, 1 ferro, 1 pólvora e 1 lápis-lazúli | 7 Cubit | Arremessada, recupera um pouco da vida e da energia dos seus Cubits |
| 11 | Carcaça da Power Bank (Power Bank Casing) | 6 quartzos, 1 ouro e 1 obsidiana | 7 Cubit | Peça das Power Banks |
| 12 | Célula de Energia Base (Base Power Cell) | 2 cobres, 2 redstones e 1 bloco de redstone | 7 Cubit | Peça da Power Bank Base |
| 13 | Módulo Avançado (Advanced Module) | 1 Núcleo Neural (5), 2 lápis-lazúlis e 2 ouros | 7 Cubit | Sobe o Cubit, a Base de Indução ou a Mesa de Engenharia para o Avançado |

### Etapa 3: com o Módulo Avançado ou a primeira célula

| Nº | Item | Materiais | Liberado por | Para que serve |
|---|---|---|---|---|
| 14 | Power Bank Base (Base Power Bank) | 11 e 12 | 12 Célula de Energia Base | Bateria portátil |
| 15 | Carcaça do Card (Card Casing) | 4 quartzos e 2 lápis-lazúlis | 13 Módulo Avançado | Peça de todo card de trabalho |
| 16 | Placa de Circuito (Circuit Board) | 5 esmeraldas, 1 comparador e 3 ouros | 13 Módulo Avançado | Peça de todo card de trabalho |
| 17 | Card Pescador (Machine Learning: Fisher) | 15, 16 e 1 vara de pescar | 13 Módulo Avançado | O Cubit pesca. Pede um Cubit Avançado |
| 18 | Card Fazendeiro (Machine Learning: Farmer) | 15, 16 e 1 enxada de ferro | 13 Módulo Avançado | O Cubit cuida da plantação. Pede um Cubit Avançado |
| 19 | Módulo Elite (Elite Module) | 1 Módulo Avançado (13), 2 corantes roxos e 2 diamantes | 13 Módulo Avançado | Sobe do Avançado para o Elite |

### Etapa 4: com o Módulo Elite ou a primeira Power Bank

| Nº | Item | Materiais | Liberado por | Para que serve |
|---|---|---|---|---|
| 20 | Power Bank Avançada (Advanced Power Bank) | 14, 2 lápis-lazúlis e 2 ouros. Ou 11 e 21 | 14 Power Bank Base, ou 21 para a segunda receita | Bateria portátil maior |
| 21 | Célula de Energia Avançada (Advanced Power Cell) | 12, 2 lápis-lazúlis e 2 ouros | 14 Power Bank Base | Peça da Power Bank Avançada |
| 22 | Card Organizador (Machine Learning: Organizer) | 15, 16 e 1 funil | 19 Módulo Elite | O Cubit organiza baús. Pede um Cubit Elite |
| 23 | Card Cozinheiro (Machine Learning: Cook) | 15, 16 e 1 defumador | 19 Módulo Elite | O Cubit cozinha. Pede um Cubit Elite |
| 24 | Card Patrulheiro Galático (Machine Learning: Galactic Patroller) | 15, 16 e 1 estrela de fogo de artifício | 19 Módulo Elite | O Cubit protege o lugar. Pede um Cubit Elite |
| 25 | Módulo Supremo (Ultimate Module) | 1 Módulo Elite (19), 2 lingotes de netherita e 2 varas de blaze | 19 Módulo Elite | Sobe do Elite para o Ultimate |

### Etapa 5: as maiores Power Banks

| Nº | Item | Materiais | Liberado por | Para que serve |
|---|---|---|---|---|
| 26 | Power Bank Elite (Elite Power Bank) | 20, 2 corantes roxos e 2 diamantes. Ou 11 e 27 | 20 Power Bank Avançada, ou 27 para a segunda receita | Bateria portátil ainda maior |
| 27 | Célula de Energia Elite (Elite Power Cell) | 21, 2 corantes roxos e 2 diamantes | 20 Power Bank Avançada | Peça da Power Bank Elite |
| 28 | Power Bank Suprema (Ultimate Power Bank) | 26, 2 lingotes de netherita e 2 varas de blaze. Ou 11 e 29 | 26 Power Bank Elite, ou 29 para a segunda receita | A maior bateria portátil |
| 29 | Célula de Energia Suprema (Ultimate Power Cell) | 27, 2 lingotes de netherita e 2 varas de blaze | 26 Power Bank Elite | Peça da Power Bank Suprema |

## Receitas de ajuda

Não são itens novos, então não têm número no mapa.

| Receita | Materiais | Liberada por |
|---|---|---|
| Religar um Cubit sem bateria | O Cubit e qualquer Power Bank. A Power Bank volta com a carga que sobrou | Qualquer Power Bank |
| Consertar um Cubit que morreu | O Cubit Danificado (Damaged Cubit) e a Caixa de Ferramentas (6), que é gasta | O Cubit Danificado ou a Caixa de Ferramentas |

## Dúvidas comuns

**Uma receita aparece escura, com um cadeado e `Locked` (Bloqueada). Por quê?**
Você ainda não teve no inventário o item que a libera. Procure o item na legenda e veja a coluna "Liberado por".

**Fiz o item, e o seguinte continua bloqueado.**
Veja se ele não ficou na saída da mesa. Tire-o de lá e deixe-o no seu inventário.

**Num mundo novo, até o Cubit e as peças aparecem bloqueados.**
Isso não é o esperado: elas já vêm liberadas. Avise a equipe do mod e diga se há outros mods no jogo. Enquanto isso, quem pode usar comandos libera a receita com `/recipe give @s cubit:general_robot`, ou todas com `/recipe give @s *`.

**A mesa não começa a fabricar.**
Confira, nesta ordem: se você apertou **+** para pedir a quantidade, se há energia, se os materiais estão na entrada, se há espaço na saída e se a mesa não recebe sinal de redstone, que a pausa. A tela mostra o motivo.

**Em que dimensão consigo os materiais?**
O primeiro Cubit, a Base de Indução e o Módulo Avançado só usam materiais do Overworld. O Tablet, as peças das Power Banks e a peça dos cards pedem quartzo, e a netherita e a vara de blaze, do tier Ultimate, vêm do Nether.

**O card não entra no Cubit.**
Cada card pede um tier mínimo do Cubit: Avançado para o Pescador e o Fazendeiro, e Elite para os demais. O Cubit Base não aceita cards.

**Preciso ter o Módulo Elite para fabricar o Supremo?**
Sim, e ele é gasto na receita. Se também quer subir um Cubit para o Elite, faça outro Módulo Elite.

**Se eu gastar o item que liberou a receita, ela bloqueia de novo?**
Não. Depois de liberada, fica liberada.

**Vejo a receita no JEI, mas não consigo fazê-la.**
O JEI mostra todas as receitas, também as bloqueadas. Quem decide é a mesa.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-23 11:57 | Acrescenta a imagem da árvore (pergunta 1976). |
| Zennd | 2026-11-23 11:36 | Cria o guia da árvore de crafts para o player (perguntas 1951, 1965 e 1975). |
