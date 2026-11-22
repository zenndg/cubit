# Trader

**Conceito:** o Cubit negocia com os aldeões marcados dentro da sua área, compra e vende o que o player pediu, na quantidade pedida, respeitando o travamento e o reabastecimento das ofertas, e descarrega o resultado num baú.

**Criado em:** 2026-11-22 16:20

**Status:** Refinado

Ele complementa [`trabalhos.md`](../trabalhos.md), cujas regras de toda área valem aqui. O documentado pode mudar se aparecer um impedimento técnico. É o "Comerciante" que aparece em "Outras ideias" de `trabalhos.md` (pergunta 1146), agora com nome e conceito.

As decisões foram fechadas pelo desenvolvedor em 2026-11-22, em cinco rodadas de perguntas, e ainda precisam entrar em `_docs/perguntas/` e em `_docs/decisoes.md`. Três pontos seguiram a recomendação, sem resposta explícita, e estão marcados como **assumido**.

## A ideia

Trocar com aldeões é uma das tarefas mais repetitivas do jogo. O Trader assume essa rotina: o player escolhe os aldeões e o que quer comprar ou vender, enche o input do Cubit, e ele faz as trocas uma a uma, respeitando os travamentos e os reabastecimentos dos aldeões, até concluir o pedido.

## Card

| Campo | Valor |
|---|---|
| ID | `cubit:ml_trader` |
| Nome | "Machine Learning: Trader" (`en_us`), "Machine Learning: Negociante" (`pt_br`) |
| Tiers | Advanced, Elite e Ultimate. O Base não aceita o card |
| Receita | Card Casing, Circuit Board e uma esmeralda, sem forma, liberada pelo Advanced Module (**assumido**) |

## Fluxo

1. O player põe o card Trader num Cubit Advanced, Elite ou Ultimate.
2. No Tablet, seleciona o Cubit e define a área de trabalho no app Área. Sem área, o app Trader avisa "defina a área primeiro" e não deixa marcar aldeões.
3. Abre o app Trader.
4. Marca os aldeões, só os que estão dentro da área, até o limite do tier. Botão direito marca, e Shift + botão direito desmarca.
5. Uma tela mostra todas as ofertas dos aldeões marcados, de compra e de venda.
6. O player escolhe os itens que quer e a quantidade de cada um, e ordena a lista por prioridade.
7. Ao confirmar, a tela mostra o que ele precisa pôr no input e em que quantidade, como na Engineering Desk.
8. Com qualquer item útil no input, o Cubit começa a trabalhar. O player pode completar o input aos poucos.

## Limites por tier

Todos os números ficam no config.

| Recurso | Advanced | Elite | Ultimate |
|---|---|---|---|
| Aldeões marcados | 5 | 10 | 20 |
| Input | 12 slots | 24 slots | 36 slots |
| Output | 12 slots | 24 slots | 36 slots |
| Tanque de XP | 10 níveis | 20 níveis | 30 níveis |

Na tela, o input e o output ficam em grades de 6 colunas, lado a lado, com o inventário do player embaixo.

## Ciclo de trabalho

1. Em cada aldeão, troca os itens do pedido na ordem de prioridade, até a oferta travar ou o item chegar à meta.
2. Quando nenhum item daquele aldeão pode mais ser trocado, passa para o próximo aldeão, em laço.
3. Com todos os aldeões travados, espera a primeira oferta destravar.
4. Com o output cheio, descarrega no baú e volta.
5. Quando um item chega à quantidade pedida, para de trocar esse item, mesmo com a oferta livre.
6. Com o pedido inteiro concluído, descarrega o output no baú, devolve o input que sobrou ao baú, avisa "pedido concluído" e hiberna.
7. Sem input para nenhuma troca pendente, descarrega o output no baú, avisa o player e hiberna.
8. **Ritmo:** 1 s entre uma troca e a próxima, no config, com uma animação curta de negociação (**assumido**).

## Regras

- **Área:** ele só negocia com aldeões dentro da área do Cubit e nunca sai dela para trabalhar.
- **Reputação:** o preço usa a reputação do dono, com os descontos dele, como os de curar um aldeão zumbi e o do Herói da Vila.
- **Compra e venda:** qualquer oferta do aldeão vale. Os itens que o aldeão compra vão no input, e as esmeraldas recebidas vão para o output.
- **Preço variável:** a demanda pode encarecer uma oferta durante o trabalho. A lista do passo 7 é uma estimativa, e a tela mostra o progresso real de cada item e quanto input ainda falta.
- **Editar o pedido:** com o Cubit trabalhando, o player pode mudar os itens, as quantidades e a ordem. Ao abrir a edição, ele pausa. Ao confirmar, recalcula e continua do ponto em que estava.
- **Aldeão que some:** se o aldeão sai da área, continua marcado e só perde a vez. O player recebe um aviso se ele ficar fora por um dia de jogo. Morto, transformado em zumbi ou com outra profissão, ele sai da marcação, o pedido é recalculado e o player recebe um aviso.
- **Aldeão indisponível:** se o aldeão está dormindo, em pânico ou durante um raid, o Cubit pula a vez dele e tenta depois.
- **Chunks:** um aldeão em chunk descarregado fica fora do ciclo até o chunk carregar. Nunca força o carregamento (regra 6).
- **Área redesenhada:** os aldeões marcados que ficarem fora da nova área saem da marcação, com aviso.
- **Espera:** enquanto as ofertas estão travadas, ou à noite, ele vai ao Dock, se o Dock estiver na área. Lá, recarrega, descarrega o XP e volta quando a primeira oferta destravar ou amanhecer. Sem Dock na área, espera parado (**assumido**).

## Descarga

O output vai para um baú marcado no app Marcar, que precisa estar dentro da área. Sem baú marcado, ele usa o armazenamento do Dock, se o Dock estiver na área.

## XP

Esta regra vale para todo Cubit, com qualquer card, e não só para o Trader.

- **Tanque:** o XP que as trocas geram vira fluido num tanque interno do Cubit, com tamanho por tier, medido em níveis e convertido em mB no config.
- **Modos:** na tela do Cubit, o player escolhe entre três modos. O padrão é voltar ao Dock.
  - **Voltar ao Dock:** com o tanque cheio, volta ao Dock, descarrega e retoma o trabalho.
  - **Descartar o excesso:** com o tanque cheio, o XP novo se perde.
  - **Descartar tudo:** não guarda XP.
- **No Dock:** ao recarregar a energia e a vida, o Cubit descarrega o XP num armazenamento novo do Dock. O limite desse armazenamento depende do tier do Dock, no config. Com o Dock cheio, o Cubit fica com o excedente.
- **Botão:** a tela do Dock tem um botão que entrega ao player todo o XP guardado.
- **Outras profissões:** as que geram XP, como Minerador, Pescador e Patrulheiro, também enchem o tanque.

## Avisos

O player recebe uma mensagem curta no chat, e a tela do Cubit mostra um ícone de estado:

| Estado | Ícone na tela |
|---|---|
| Trocando | Olhos normais, com uma esmeralda pequena girando |
| Esperando reabastecer | Relógio |
| Output cheio, indo ao baú | Baú |
| Hibernando | Olhos fechados |

Exemplo de aviso: "Trader: sem esmeraldas para continuar, 34 itens no baú."

## Visual e personalidade

- **Peça:** uma bolsa de couro tiracolo, com fivela de esmeralda.
- **Tela:** os olhos viram esmeraldas por um instante quando uma troca fecha.
- **Falas:** um vendedor simpático e um pouco exagerado. Exemplos:
  - "Negócio fechado!"
  - "Esse preço tá salgado, volto amanhã."
  - "Sem esmeraldas, chefe. Vou tirar um cochilo."
  - "Pedido completo! Tá tudo no baú."

## Em aberto

1. **Tanque em mB:** a conversão de níveis em mB precisa seguir a curva de XP do jogo, que não é linear. Recomendação: guardar pontos de XP, e não níveis, e mostrar o equivalente em níveis na tela.
2. **XP de outras profissões:** a regra do tanque muda o comportamento de cards que já existem. Recomendação: registrar a decisão nos documentos de cada profissão afetada.
3. **Tela do Dock:** o armazenamento de XP e o botão mudam a tela do Dock. Recomendação: desenhar a mudança junto com o prompt das imagens do Trader.
4. **Livros encantados:** a lista de ofertas mostra o encantamento e o nível de cada livro, para separar ofertas diferentes do mesmo item.
5. **Imagens:** a prancha conceito, o card, a bolsa e os ícones de estado ficam num `prompt.md` depois que este conceito for aceito.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-22 16:46 | Cria o conceito do Trader no modelo de trabalho, com as decisões das cinco rodadas. |
