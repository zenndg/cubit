# Farm de trigo num chunk com o Cubit Fazendeiro

Passo a passo para montar uma plantação de trigo dentro de um chunk, com um Cubit que ara, planta, colhe e guarda sozinho. Vale para o mod como está hoje, com a Network até a entrega 5.

## O que você vai precisar

| Item | Quantidade | Para quê |
|---|---|---|
| Cubit | 1 | O robô que trabalha |
| Advanced Module | 1 | Sobe o Cubit para o tier Advanced, o primeiro que aceita o card do Fazendeiro |
| Machine Learning: Farmer | 1 | O card que dá ao Cubit o trabalho de Fazendeiro |
| Cubit Tablet | 1 | Desenha a área, marca o baú e o Dock |
| Induction Dock | 1 | Onde o Cubit recarrega |
| Baú | 1 | Guarda as sementes e recebe a colheita |
| Sementes de trigo | 1 ou 2 pilhas | Para o primeiro plantio |
| Baldes de água | Alguns | Para as valas de irrigação |
| Farinha de osso | Opcional | Para o slot de fertilizante |

Para o teste com a rede, opcional, você também precisa de 1 Network Server, 1 Solar Relay e 1 Signal Antenna.

No criativo, todos os itens do mod estão na aba do Cubit. No sobrevivência, eles saem da Engineering Desk.

## Números que importam

Os valores abaixo são os padrões do config, para o Cubit Advanced.

| O quê | Valor |
|---|---|
| Tamanho máximo da área | 16 × 16 blocos, o tamanho exato de um chunk |
| Raio de chamada do Dock | 24 blocos, do Dock ao centro da área |
| Altura máxima da área | 8 blocos |
| Baús de descarga | 1 |
| Água para arar | A até 4 blocos da terra |
| Custo por planta colhida ou bloco arado | 50 FE |
| Pausa entre uma ação e a próxima | 20 ticks, 1 segundo |
| Intervalo de descarga no baú | 5 minutos, ajustável na aba Trabalho |

O Elite e o Ultimate têm áreas maiores, de 32 × 32 e 64 × 64, e colhem também cana, cacto, abóbora e melancia. Para caber num chunk só, o Advanced é o tier certo.

## Antes de começar: duas coisas desta versão

1. **Os apps que guiam o Cubit são o Área, o Descarga e o Dock.** O app Network já marca Docks e baús, mas o Cubit ainda não os usa para trabalhar: isso entra na entrega 6 da Network. Hoje, o baú das sementes é marcado no app Descarga, e o Dock preferido, no app Dock.
2. **A rede ainda não é obrigatória.** A farm funciona com ou sem antena. Com a antena, a área fica presa ao chunk dela, e a antena manda energia ao Dock.

## Parte 1: escolher e preparar o chunk

1. Aperte **F3 + G** para ver as bordas dos chunks.
2. Escolha um chunk plano, de grama ou terra. Um mundo superplano facilita o primeiro teste.
3. Limpe o chunk: tire flores, grama alta e qualquer bloco em cima do chão. O Cubit só ara um bloco com **ar em cima**.
4. Conte as colunas do chunk de 0 a 15, a partir da borda oeste, onde a coordenada X é múltiplo de 16.
5. Cave duas valas de 1 bloco de profundidade, de ponta a ponta do chunk, nas colunas **4** e **11**, e encha-as de água.
   - Com elas, todo bloco do chunk fica a até 4 blocos de alguma água, e a terra arada não seca.
   - Para encher uma vala inteira com poucos baldes, ponha água nas duas pontas e deixe as fontes se formarem no meio.
6. Coloque o **baú** numa quina do chunk, **dentro** dele, e ponha as **sementes de trigo** nele.
   - Se quiser, ponha também farinha de osso. O Cubit busca no baú quando o slot de fertilizante esvazia.

Visto de cima, o chunk fica assim, com `~` para a água, `.` para a terra e `B` para o baú:

```text
B . . . ~ . . . . . . ~ . . . .
. . . . ~ . . . . . . ~ . . . .
. . . . ~ . . . . . . ~ . . . .
(as 16 linhas seguem iguais)
```

## Parte 2: preparar o Cubit Fazendeiro

7. Coloque o Cubit **dentro do chunk**, com o clique direito do item no chão.
   - Na primeira vez, ele pede um nome. O nome ajuda a achá-lo no Tablet.
8. Aplique o **Advanced Module** no Cubit.
   - O topo da tela dele passa a mostrar o tier Advanced.
9. Abra a tela do Cubit com o **clique direito** nele.
10. Ponha o card **Machine Learning: Farmer** no slot do card, na fileira de slots exclusivos.
    - O Cubit ganha o chapéu de palha e a mochila com a enxada.
11. Opcional: ponha farinha de osso no **slot de fertilizante**, que aparece ao lado do card.
    - Cada planta recém-plantada recebe um uso e cresce mais rápido.
12. Confira a **bateria**. Se estiver baixa, carregue antes, no Dock ou com uma Power Bank.

## Parte 3: o Dock, sem a rede

Se for usar a rede, pule para a Parte 4.

13. Coloque o **Induction Dock perto do chunk**, a até **24 blocos do centro dele**, o raio de chamada do Cubit Advanced. Na prática, até uns 16 blocos para fora da borda do chunk.
    - Mais longe que isso, a área é recusada ao aplicar.
14. Dê energia ao Dock:
    - pelo **painel solar** dele, que gera de dia com o céu aberto;
    - por **Power Banks** no inventário dele;
    - ou por cabos de outro mod, se houver.
15. Pegue o Tablet e troque para o app **Dock**: segure o Tablet e use **Shift + roda do mouse** até a barra de ação mostrar `Cubit Tablet · Dock`.
16. Clique no **Cubit** para escolhê-lo e depois no **Dock**.
    - Ele vira o Dock preferido do Cubit, para onde ele volta quando a bateria baixa.

## Parte 4: o Dock com a rede (opcional)

Esta parte testa junto a entrega 5 da Network.

17. Monte o **Network Server** e o **Solar Relay** num chunk vizinho ao da farm. Confira na tela do Servidor que ele dá a partida.
18. Clique com o item da **Signal Antenna** no Servidor para ligá-la, e coloque-a **dentro do chunk da farm**.
19. Coloque o **Induction Dock dentro do chunk da farm**.
20. No Tablet, troque para o app **Network**, o último da lista, na cor rosa.
21. Clique no **Cubit** para escolhê-lo, depois na **antena**, depois no **Dock** e depois no **baú**.
    - Aparecem setas sobre os três: na cor do canal sobre a antena, vermelha sobre o Dock e laranja sobre o baú.
    - Na aba do Dock, a linha `Network +X FE/t` mostra a energia que chega pela antena.
22. Faça também os passos **15 e 16**, no app **Dock**, porque o app Network ainda não define o Dock preferido.

## Parte 5: desenhar a área e soltar o Cubit

23. Troque o Tablet para o app **Área**.
24. Clique no **Cubit** para escolhê-lo.
25. Clique na **primeira quina do chunk**, no bloco do chão.
26. Clique nas outras três quinas, uma de cada vez, em **ângulo reto**, seguindo a borda do chunk.
    - A linha do desenho aparece em vermelho enquanto a forma está aberta.
    - Com a antena, um ponto fora do chunk dela é recusado na hora, e a barra de ação diz o motivo.
27. Clique de novo na **primeira quina** para fechar a forma.
    - A linha fica **verde**.
28. Faça **Shift + clique** com a linha verde.
    - A área vai para o Cubit, e ele caminha até ela.
    - O topo da tela dele muda para `Working · Farmer`.
29. Troque o Tablet para o app **Descarga** e clique no **baú das sementes**.
    - Como o baú está dentro da área, é dele que o Cubit tira as sementes, e é nele que descarrega o trigo.

Se errar um ponto, Shift + clique com a linha vermelha desfaz o último.

## Parte 6: ver a farm funcionando

30. **Arar:** o Cubit vai aos blocos de grama ou terra perto da água e os ara, um por vez.
31. **Plantar:** sem semente no inventário, ele vai ao baú, pega até uma pilha e planta. Ele forma canteiros com a mesma semente dos vizinhos.
32. **Crescer:** o trigo leva alguns dias do jogo para amadurecer. Para testar mais rápido:
    - no criativo, rode `/gamerule randomTickSpeed 300`, e volte ao padrão com `/gamerule randomTickSpeed 3`;
    - ou use farinha de osso, à mão ou pelo slot de fertilizante.
33. **Colher e replantar:** o trigo maduro é colhido, e o Cubit replanta na hora com a semente da própria colheita.
34. **Guardar:** a cada intervalo de descarga, ele leva o trigo e as sementes que sobraram ao baú.
    - O intervalo fica na aba **Trabalho** da tela do Cubit.
35. **Recarregar:** com a bateria baixa, ele vai sozinho ao Dock preferido, carrega e volta à área.

## Problemas comuns

| O que aparece | Por quê | O que fazer |
|---|---|---|
| A área é recusada ao aplicar | O centro dela está longe do Dock, ou a área passa de 16 × 16 ou da altura do tier | Aproxime o Dock ou redesenhe a área dentro do chunk |
| Um ponto é recusado na hora | Fora do ângulo reto, sobre o próprio desenho ou, com a antena, fora do chunk dela | Clique num bloco em linha reta com o ponto anterior |
| O Cubit não ara alguns blocos | Falta água a até 4 blocos, ou há algo em cima do bloco | Confira as valas e limpe o chão |
| O Cubit não planta | Não há semente no inventário dele nem no baú da Descarga dentro da área | Ponha sementes no baú e confira se ele está marcado no app Descarga |
| `Hibernating · Inventory full` | O inventário encheu, e o baú não tem espaço | Esvazie o baú e use o `Resume` da aba Trabalho |
| O Cubit para e desliga | A bateria acabou sem um Dock preferido ao alcance | Marque o Dock no app Dock e confira se o Dock tem energia |
| O card não entra no slot | O Cubit ainda é Base | Aplique o Advanced Module antes |

Se algo não andar, o topo da tela do Cubit e a barra de ação quase sempre dizem o motivo.
