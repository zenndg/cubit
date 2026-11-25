# Backlog

As ideias do desenvolvedor para o futuro, anotadas para não ficarem só na conversa. Nada aqui está decidido nem autorizado: cada ideia vai a uma rodada de refinamento quando entrar no roadmap, e só então vira um conceito em `funcionalidades/` (regras 11 e 54 do [`AGENTS.md`](../AGENTS.md)). Os pontos em aberto são só um ponto de partida para essa rodada.

Uma ideia que já tem documento próprio só aparece aqui com o link, para ter um lugar único de consulta.

## Tablet

1. **Lista de todos os Cubits do dono** (pergunta 1747).
   - **A ideia:** a lista inicial do Tablet mostra todos os Cubits que o dono colocou no mundo, paginada, com busca pelo nome e filtros por profissão. A ordem é pela última interação do dono com o Cubit e, em seguida, pela ordem em que cada um foi colocado no mundo.
   - **Em aberto:** o que conta como interação; onde fica guardada a ordem em que cada Cubit foi colocado, que hoje não existe no save; e como aparecem os Cubits em chunk descarregado, que hoje o Tablet mostra pela foto do Cubit (`RobotSnapshot`).
2. **Puxar o Cubit para o inventário** (pergunta 1747).
   - **A ideia:** um botão no Tablet traz o Cubit para o inventário do dono, de onde ele estiver e em qualquer situação.
   - **Em aberto:**
     - o Cubit num chunk descarregado, que o mod nunca carrega à força (regra 22);
     - não duplicar nem perder o Cubit e os itens dele (pilar Segurança);
     - o Cubit em outra dimensão, no Dock, hibernando ou morto, como Damaged Cubit;
     - o inventário do dono cheio;
     - o custo, se houver.
3. **Tablet com tiers** (pergunta 1747).
   - **A ideia:** o Tablet tem tiers, e cada funcionalidade entra conforme o tier: o Base tem alguns recursos, e os tiers seguintes vão acrescentando.
   - **Em aberto:**
     - como o Tablet sobe de tier, se pelos Modules, como o Cubit e o Dock;
     - quais apps e botões ficam em cada tier;
     - o que acontece com os Tablets que já existem nos mundos, que hoje têm tudo (regra 18).

## Todo Cubit

1. **Nome ao colocar o Cubit** (pergunta 1748). Entrou no Dock unificado, na issue #246, refinado nas perguntas 1874 a 1881, com o conceito em [`personalizacao.md`](funcionalidades/personalizacao.md#nome).

2. **XP em tanque de líquido** (pergunta 1762). Virou o XP Cubit e Dock, refinado e autorizado nas perguntas 1814 a 1851, na issue #244, com o conceito em [`xp.md`](funcionalidades/xp.md).

3. **Tier acima do Ultimate** (pergunta 2057).
   - **A ideia:** um quinto tier, acima do Ultimate, feito com a estrela do Nether, que o Wither solta, e outros dos itens mais raros do Minecraft. Os números são o dobro dos do Ultimate, e ele só pode ser aplicado no Cubit, não no Dock nem na Engineering Desk.
   - **Em aberto:**
     - o nome, a cor do colar e o visual do tier;
     - os outros itens raros da receita, só do jogo puro, e o que libera a receita;
     - o que dobra: nem tudo comporta o dobro, como a velocidade, que iria a 3,5×, o inventário, que iria a 108 slots, e a lanterna, que já está na luz 15, o máximo do jogo;
     - os recursos por tier que não são números, como os modos de defesa e os trabalhos;
     - a Power Bank, que tem uma por tier, com 70% da bateria, e a recarga de um Cubit desse tier num Dock Ultimate;
     - o Module desse tier usado no Dock e na mesa, que hoje aceitam todos;
     - os saves, que guardam o tier, e o config, que tem chaves por tier (regra 18).

4. **Acessórios do Cubit** (pergunta 2096).
   - **A ideia:** peças que o player adiciona ao Cubit ou remove dele. Exemplos: um gancho de rapel, que puxa o Cubit para passar por buracos, e um propulsor, para ele pular alturas de 2 ou mais blocos.
   - **Em aberto:**
     - onde os acessórios ficam, se em slots próprios, quantos e a partir de qual tier;
     - as receitas, só com materiais do jogo puro, o consumo e o que libera cada um;
     - o visual e a animação de cada um, com o chapéu e a mochila das profissões;
     - o que muda no caminho que o Cubit calcula, e o que acontece sem bateria no meio do uso;
     - como convivem com as asas do Patrulheiro, que já fazem voos curtos;
     - o estado no save e no item (regra 18).

## Engineering Desk

1. **Desmontar itens do mod** (pergunta 2504).
   - **A ideia:** uma aba nova na mesa, `Desmontar`, com uma tela própria, liberada no tier Elite. O player põe de um lado o item do mod que quer desmontar, e o outro lado mostra o que ele recupera. Ele pede para iniciar, e os itens vão para a saída da mesa.
   - **O que desmonta:** todo item do mod cuja receita usa outros itens do mod.
   - **O que volta:** só os itens do próprio mod usados na receita. Os itens do jogo não voltam.
   - **Tempo e custo:** os mesmos de criar o item, em ticks e em FE.
   - **Por quê:** as peças do mod servem em receitas diferentes, e desmontar permite reutilizá-las.
   - **Quando:** na entrega 3 da Network (#265), depois das luzes do Servidor (pergunta 2505). Vai ao refinamento antes do código.
   - **Em aberto:**
     - um nível por vez ou até o fim da cascata, como o Cubit que vira as peças ou também as peças delas;
     - o item com estado, para nada se perder nem se repetir (pilar Segurança): o inventário, a energia, o XP, o nome, o card e as tochas de um Cubit, o estoque e os itens de um Dock, a carga de uma Power Bank, o que guarda o Network Server e a própria mesa. Recusar, esvaziar antes ou devolver;
     - o item que subiu de tier, e se o Module volta;
     - o item que tem mais de uma receita, como as Power Banks, que se fazem pela carcaça ou pelo upgrade, e qual delas vale;
     - as receitas especiais, do conserto e do religar, e o Damaged Cubit;
     - as receitas mudadas por um pack, para montar e desmontar em ciclo nunca criar itens (regra 4);
     - a marca que aceita vários itens, e o que volta no lugar dela;
     - um por vez ou uma quantidade, como no pedido, com a saída cheia, sem energia e com o sinal de redstone;
     - como convive com um pedido e com a cascata, que hoje travam as abas, e onde fica o item enquanto desmonta, na entrada ou num espaço próprio, também ao quebrar a mesa;
     - a automação pelas faces, o comparador, o Jade e o JEI;
     - os números no config (regra 3), o visual, os sons e a animação.

## Robô de transporte

1. **Um segundo robô, o caminhão** (pergunta 2197).
   - **A ideia:** além do Cubit, um robô em forma de caminhão, com uma única função, o transporte logístico. Ele traz os recursos de cada farm para a base principal. A cada tier, fica mais rápido e tem mais energia, e cada slot do inventário dele tem a quantidade da pilha dobrada.
   - **Quando:** logo depois da Network completa, e antes das outras profissões.
   - **Outros recursos:** o pedido os cita sem dizer quais, e a lista fica para o refinamento.
   - **Em aberto:**
     - a regra do robô único do [`AGENTS.md`](../AGENTS.md#robô-único), que diz que o Cubit é o único robô do mod, e o que vale para o novo nas regras 39 a 43;
     - o nome, o ID e o visual, sem copiar um robô ou veículo existente (regra 9);
     - o que dobra: a pilha de cada tier em relação ao anterior, como 64, 128, 256 e 512, ou o dobro do jogo em todos os tiers;
     - a pilha acima de 99, que o jogo não salva num slot comum (`ItemStack.CODEC` vai de 1 a 99), e a dos itens que empilham até 16 ou 1;
     - de onde ele tira e onde põe os itens: o baú de descarga, o inventário do Servidor, que hoje não recebe nada pela rede, ou outro;
     - o caminho entre a farm e a base, com chunks descarregados no meio, que ele não pode carregar à força (regra 22): sem player por perto, um robô para ao sair de um chunk ativo;
     - se ele anda de verdade, por terreno difícil e água, e como sobe e desce;
     - a rota, as paradas, o Dock em que recarrega e os apps do Tablet;
     - quantos por player e o desempenho;
     - o que o separa do Organizador, que move itens na área, do storage wireless (#207) e do Logístico, descartado na pergunta 1146;
     - morte, conserto, recolher com a carga dentro, sem perder nem duplicar itens, e se ele usa cards.

## Profissões

1. **Scanner de minérios só no Minerador** (pergunta 1741). Quando o Minerador começar, o scanner de minérios sai de todo Cubit e passa a ser só do Minerador, melhorado. Está anotado em [`trabalhos/minerador.md`](funcionalidades/trabalhos/minerador.md).
2. **Explorador** (perguntas 1668 a 1671). Profissão nova, anotada em [`trabalhos/explorador.md`](funcionalidades/trabalhos/explorador.md).
3. **Busca no fim da escolta do Patrulheiro** (pergunta 1766).
   - **A ideia:** hoje, a cada mob que sai do nível, por morte, despawn ou mudança de dimensão, o `RobotEscort.onLeave` percorre os robôs do nível para achar quem o escoltava. Medir o custo com muitos mobs e Cubits e, se pesar, trocar por um registro dos escoltados no servidor (regra 21).
   - **Em aberto:** a medida, numa farm de mobs com 20 a 30 Cubits, e se o registro vale o código a mais (pilar Simplicidade).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-25 19:44 | Leva desmontar itens à entrega 3 da Network (pergunta 2505). |
| Zennd | 2026-11-25 19:06 | Acrescenta desmontar itens na Engineering Desk (pergunta 2504). |
| Zennd | 2026-11-25 17:33 | Tira o Cubit que se perde num teleporte longo, que chamar o Cubit resolve (pergunta 2461). |
| Zennd | 2026-11-24 13:34 | Acrescenta o robô de transporte (pergunta 2197). |
| Zennd | 2026-11-24 08:35 | Acrescenta os acessórios do Cubit (pergunta 2096). |
| Zennd | 2026-11-23 20:27 | Acrescenta o tier acima do Ultimate (pergunta 2057). |
| Zennd | 2026-11-23 00:00 | Troca o nome ao colocar o Cubit pelo link do conceito (perguntas 1874 a 1881). |
| Zennd | 2026-11-22 23:13 | Troca o XP em tanque de líquido pelo link do conceito, refinado nas perguntas 1814 a 1851. |
| Zennd | 2026-11-22 21:03 | Leva o XP em tanque de líquido ao roadmap, como XP Cubit e Dock (perguntas 1787 a 1789). |
| Zennd | 2026-11-22 17:00 | Acrescenta a busca no fim da escolta do Patrulheiro (pergunta 1766). |
| Zennd | 2026-11-22 16:34 | Acrescenta o XP em tanque de líquido, no Cubit e no Dock (pergunta 1762). |
| Zennd | 2026-11-22 23:00 | Acrescenta o nome pedido ao colocar o Cubit (pergunta 1748). |
| Zennd | 2026-11-22 22:40 | Cria o backlog com as ideias do Tablet, do teleporte longo, do scanner do Minerador e do Explorador (perguntas 1741, 1746 e 1747). |
