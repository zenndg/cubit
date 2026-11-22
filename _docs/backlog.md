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

## Profissões

1. **Scanner de minérios só no Minerador** (pergunta 1741). Quando o Minerador começar, o scanner de minérios sai de todo Cubit e passa a ser só do Minerador, melhorado. Está anotado em [`trabalhos/minerador.md`](funcionalidades/trabalhos/minerador.md).
2. **Explorador** (perguntas 1668 a 1671). Profissão nova, anotada em [`trabalhos/explorador.md`](funcionalidades/trabalhos/explorador.md).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-22 22:40 | Cria o backlog com as ideias do Tablet, do teleporte longo, do scanner do Minerador e do Explorador (perguntas 1741, 1746 e 1747). |
