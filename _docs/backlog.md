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

1. **O Cubit que se perde num teleporte longo do dono** (pergunta 1746).
   - **A ideia:** no instante do teleporte do dono, o Cubit que segue e ainda está carregado vai junto, como no renascer (`RobotRespawn`), sem carregar chunk à força (regra 22).
   - **Por quê:** hoje o teleporte do seguir roda no tick do Cubit, e num teleporte longo o chunk dele sai da simulação antes de ele ver o dono longe.
   - **Quando:** numa issue própria, depois do PR 3 do Patrulheiro.
   - **Em aberto:** o teleporte para outra dimensão.

2. **Nome ao colocar o Cubit** (pergunta 1748).
   - **A ideia:** ao colocar um Cubit no mundo, abre uma janela que pede o nome dele. Sem nome escolhido, ele se chama `Cubit` mais 8 caracteres do seu UUID.
   - **Em aberto:**
     - se a janela abre só na primeira colocação ou em toda colocação;
     - como ela convive com as frases de apresentação da primeira colocação (perguntas 1672 a 1682);
     - os Cubits que já existem nos mundos, que hoje podem não ter nome (regra 18);
     - a regra do nome que o Tablet já aplica (`RobotName`).

3. **XP em tanque de líquido** (pergunta 1762).
   - **A ideia:** um inventário novo, de líquido: o XP dentro do Cubit vira um tanque de XP líquido, com as conversões iguais às do mod Industrial Foregoing. O tanque enche quando o Cubit faz algo que gera XP, como um player. Quando ele vai ao Dock, passa o XP para um tanque próprio do Dock, e o player, interagindo com o Dock, puxa o XP para si.
   - **Em aberto:**
     - a taxa de conversão entre ponto de XP e líquido, conferida no Industrial Foregoing, e como ela fica no config (regra 3);
     - o mod funcionar sem o Industrial Foregoing (regra 50) e se o líquido conversa com os tanques de outros mods, pela capability de fluido do NeoForge;
     - o que gera XP para o Cubit, além do abate do Patrulheiro;
     - a convivência com o XP como energia do Patrulheiro (perguntas 1728 a 1730 e 1759);
     - o tamanho do tanque por tier, no Cubit e no Dock;
     - como o player puxa o XP do Dock: tudo de uma vez, por nível ou por quantidade;
     - o tanque no item do Cubit e do Dock, nos saves (regra 18);
     - o visual na tela do Cubit e do Dock.

## Profissões

1. **Scanner de minérios só no Minerador** (pergunta 1741). Quando o Minerador começar, o scanner de minérios sai de todo Cubit e passa a ser só do Minerador, melhorado. Está anotado em [`trabalhos/minerador.md`](funcionalidades/trabalhos/minerador.md).
2. **Explorador** (perguntas 1668 a 1671). Profissão nova, anotada em [`trabalhos/explorador.md`](funcionalidades/trabalhos/explorador.md).
3. **Busca no fim da escolta do Patrulheiro** (pergunta 1766).
   - **A ideia:** hoje, a cada mob que sai do nível, por morte, despawn ou mudança de dimensão, o `RobotEscort.onLeave` percorre os robôs do nível para achar quem o escoltava. Medir o custo com muitos mobs e Cubits e, se pesar, trocar por um registro dos escoltados no servidor (regra 21).
   - **Em aberto:** a medida, numa farm de mobs com 20 a 30 Cubits, e se o registro vale o código a mais (pilar Simplicidade).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-22 17:00 | Acrescenta a busca no fim da escolta do Patrulheiro (pergunta 1766). |
| Zennd | 2026-11-22 16:34 | Acrescenta o XP em tanque de líquido, no Cubit e no Dock (pergunta 1762). |
| Zennd | 2026-11-22 23:00 | Acrescenta o nome pedido ao colocar o Cubit (pergunta 1748). |
| Zennd | 2026-11-22 22:40 | Cria o backlog com as ideias do Tablet, do teleporte longo, do scanner do Minerador e do Explorador (perguntas 1741, 1746 e 1747). |
