# Cubit

Mod de Minecraft que adiciona um robô companheiro fofo, movido a bateria, de tecnologia e construção. É feito para o player e funciona numa instância só com ele e suas dependências. O modpack [Iron Meridian](https://github.com/zenndg/iron-meridian) é um dos lugares onde ele é testado.

**Comece por:** [`README.md`](README.md), a visão geral com as fases, e [`_docs/README.md`](_docs/README.md), o mapa da documentação. As respostas em `_docs/perguntas/` são a fonte da verdade e prevalecem sobre os outros documentos.

## Estado atual

- **Etapa atual:** as Fases 1, 2 e 3 foram encerradas, e a primeira versão, `0.1.0`, saiu com tag e release no GitHub, sem envio a nenhum site (respostas 2171 e 2186). A Fase 4 "Tier e ataque" também foi encerrada: os tiers, os Modules e as receitas (PRs #145 e #147), o choque e os modos de defesa (#149), o modo Focus (#151) e as chaves da aba Settings (#153). A instância só com o mod e as confirmações que saíram da `0.1.0` foram feitas (resposta 820). A `0.1.1` foi fechada com a Fase 4 "Tier e ataque" (PR #159), e a Fase 4.1 também foi encerrada: a coleta e o ímã (PR #163), a Power Bank (PR #166), a busca dos itens do chão (PR #167), o inventário do Induction Dock com a saída do Cubit de bateria e vida cheias (PR #169) e as tochas automáticas com o olhar para o alvo do choque (PR #171). A fornalha foi para o Minerador, na Fase 5, e o carregador de ferramentas saiu do mod (resposta 930). A Fase 4.2 também foi encerrada, e com ela a Fase 4 inteira: o Tablet (PRs #177 a #179), o retorno automático com o `Follow` (#182), o modo área (#183), a descarga em baú (#185), os ícones das chaves (#187) e o local de morte (#189) (respostas 1133 a 1135). O trabalho entra por issue, na ordem do `checklist.md`.
- **Já entrou:** colocar e recolher, seguir o player, vários robôs, fogo, lava, os arquivos de som, a revisão dos `.md`, o novo modelo, a morte com o item danificado, o inventário, a energia em uso, o consumo por situação, a reação a blocos que ferem, o Induction Dock, o `/cubit reset`, as receitas e, na Fase 3, os sons, as expressões, o som de dano, a barra de vida, as animações, o colar e a bateria, os textos curtos, a lanterna, o painel solar, a cor da carcaça, o nome, a detecção dos scanners, com chave e filtro, numa esfera, as caixas dos scanners, a seta sobre o mob, as cores das antenas com o halo do corpo e as falas, inclusive sobre a redstone minerada. Também entraram a volta do Cubit com o dono que renasce, a descida de até 5 blocos, o Jade e as telas próprias do Cubit e do Induction Dock.
- **Próximos:** a Fase 5 está em andamento, como um kanban no GitHub Project "Cubit · Fase 5", um card por vez, na ordem do `checklist.md` (respostas 1136 a 1158). A consolidação (#192), a base do trabalho (#193), o Pescador (#194), o Fazendeiro (#195), o controle de velocidade (#212), o Organizador (#196) e o Cozinheiro (#197) já entraram. Antes do Patrulheiro Galático, entraram cards fora do plano: as receitas novas (#219), as peças de montagem (#221), as portas marcadas (#223) e os tiers do Dock (#225). Também entraram o Dock Solar (#227), depois unificado em todo Dock pela #246, e a Recharge Splash (#229). Com prioridade sobre o resto do roadmap, vieram quatro itens, nesta ordem (respostas 1338, 1359, 1383 e 1424): a Engineering Desk (#231), a mesa de criação de onde saem todos os itens do mod, que já entrou, em quatro PRs (#232 a #235), com as animações, a tela de prancheta, as categorias do catálogo, o Jade e o JEI; a revisão dos trabalhos por tier, que ficou sem mudança (resposta 1539); a revisão das receitas dos cards (#236), com a Card Casing e a Circuit Board, que já entrou (#237); e o Patrulheiro Galático (#198), no lugar do Defensor, que já entrou, em quatro PRs (respostas 1605, 1631, 1676 e 1749): o comportamento, com as falas das profissões, que já entrou (#238); a rota no app Marcar, com a ronda lenta e as paradas, que já entrou (#239); o PR 3, que já entrou (#242), com o realismo da ronda e do combate, os alvos novos, com as tags, a equipe de Patrulheiros, com o cerco e o socorro, as armas contra o enderman, o breeze e a bola de fogo do ghast, as asas para se mover, a escolta marcada, com o app Escort, o XP dos abates como energia, o retorno ao Dock com 5% da bateria no Elite e 3% no Ultimate, as frases de apresentação de todo Cubit, o estado do Patrulheiro sincronizado com o cliente, para o visual do PR 4, e as correções que as GameTests e a auditoria mostraram (respostas 1623 a 1698 no conceito, 1700 a 1758 nas entregas e 1759 a 1766 nas correções); e o PR 4 (#243), que fechou a #198, com o modelo, as texturas, com o capacete e a mochila em grafite, as animações, com o visor à frente dos olhos e as asas maiores, a tela com o visor baixado, o brilho no escuro, o globo desenhado, a lâmina maior e acesa por mais tempo, a textura do card e os 11 sons (respostas 1767 a 1776, 1780 a 1786 e 1790 a 1812). A issue própria para o Cubit que se perde num teleporte longo do dono segue em `_docs/backlog.md`, sem ordem decidida (respostas 1746 e 1779). Depois do Patrulheiro, passou à frente do XP o Dock unificado (#246, resposta 1852), que já entrou: o painel solar em todo Dock, que se transforma em 6 s, com os armários laterais, os braços, o 3º bloco, as luzes, os sons e a textura final, e os nomes do Dock e do Cubit (respostas 1852 a 1925). O conceito do XP Cubit e Dock entrou só em documentos (#245, respostas 1814 a 1851). Depois do Dock unificado, entrou a luz da lua no Dock Ultimate (#248, respostas 1941 a 1963), que gera também à noite com o céu limpo, com uma lua na tela do Dock. Com prioridade CRÍTICA, a #250 entrou em parte e continua aberta (resposta 1951): entraram o guia do player com a árvore de crafts e a imagem (PR #251) e o pedido na Engineering Desk (PR #252), em que toda receita pode ser vista e o + só sai de zero com a receita liberada e os materiais, com a liberação protegida e o teste da árvore alcançável (respostas 1982 a 1995). Faltam as conquistas visíveis, uma por item (respostas 1968, 1972 e 1973), que ficaram para outro dia (resposta 2005). O bloqueio visto num mundo novo não se repetiu e ficou como falso positivo naquele cenário (resposta 2002). Também foi concluída a fabricação em cascata, o recurso da Engineering Desk no último tier (#253, respostas 1964 e 2006 a 2056), em dois PRs: o PR 1 (#254), com o plano, o estoque, os saves e a tela simples, e o PR 2, que fecha a #253, com a tela dos dois passos, o ícone, o Jade, a dica do item e o guia do player. Depois dela, entrou o XP Cubit e Dock (#244), o XP em tanque de líquido, em dois PRs: o PR 1 (#257), o Cubit, com a ida e a volta do Dock sem caminho (respostas 2090 a 2097), e o PR 2 (#258), que fechou a #244, o Dock, com os quatro modos, a conversão em energia, os canos, as abas XP do Dock e do Tablet e o `Unload XP`, com a troca do clique do Dock: o clique direito abre a tela, e o Shift + clique chama ou libera o Cubit (respostas 2099 a 2130). Depois do XP, entraram as peças compartilhadas (#259): oito peças com uma função comum a vários itens, numa categoria própria da Engineering Desk, com as receitas novas do Cubit, do Dock, do Tablet, dos cards e da Power Bank Casing, o Neural Core e a Circuit Board com IDs novos, e o guia e a árvore refeitos (respostas 2118 e 2131 a 2180). Está em andamento a Network, a rede sem fio, com o Servidor, o Solar Relay e a antena, em 9 entregas, cada uma com a branch saindo da `integration/network`, a centralizadora da feature, e o PR apontando para ela (#263 a #271, respostas 2196 a 2378), com o conceito em [`network.md`](_docs/funcionalidades/network.md). Já entraram nela a entrega 1, o conceito (PR #272), e a 2, a área desenhada (PR #274). O código de cada entrega espera a autorização própria (resposta 2224), e a `develop` só recebe o PR da feature completa, na `0.2.0` (respostas 2205, 2314 e 2378). As #205 e #206 fecharam sem implementação (resposta 2292). Depois dela vêm as profissões, nesta ordem: Lenhador, Pecuarista, Mecânico, Trader, Explorador, Alquimista, Minerador e Construtor, cada profissão com o seu visual (respostas 1666, 1669, 1927 e 1930). Cada card só vai ao código depois do refinamento. A conversa sobre o `Stop` do Tablet (resposta 1076) continua pendente. A `0.1.1` também saiu só no GitHub, e o envio das duas ao Modrinth e ao CurseForge está pendente: ele ganha uma issue nova quando o desenvolvedor decidir publicar (respostas 2186 e 2188).
- **Comandos:** estão em [`_docs/desenvolvimento/ferramentas-de-build.md`](_docs/desenvolvimento/ferramentas-de-build.md).
- **Limite:** não avance para as fases seguintes sem pedido explícito.

## Documentação

- **Números:** ficam em `_docs/tiers.md`. As outras páginas apontam para lá. O `README.md` traz só um resumo, e o roteiro manual cita os valores padrão como resultado esperado.
- **Perguntas:** ficam em `_docs/perguntas/`, com um arquivo por fase. Recomendação de pergunta pendente não vale como decidida.
- **Decisões:** o resumo do que foi decidido fica em `_docs/decisoes.md`.
- **Histórico:** todo `.md` em `_docs/` termina com uma tabela de histórico de modificações, e quem altera o arquivo adiciona uma linha no topo dela, em nome de `Zennd`. As linhas antigas, em outro nome, continuam como estão (respostas 1482 e 1484). Fora de `_docs/`, os `.md` não têm essa tabela.
- **Imagens:** ficam em `_docs/imagens/`.
- **Backlog:** as ideias do desenvolvedor para o futuro, ainda sem decisão, ficam em `_docs/backlog.md`, e cada uma vai ao refinamento quando entrar no roadmap (resposta 1747).
- **Rascunhos:** conversas e ideias vão para `tmp/`, que o Git ignora. Rascunhos de PR ficam em `tmp/pr/`, arquivos de issue, em `tmp/issue/`, logs, em `tmp/log/`, e os prompts de imagem e as imagens geradas por eles, em `tmp/arte/{nome}/`.
- **Fluxo de trabalho:** o passo a passo de uma tarefa, de uma versão e de um hotfix está em [`_docs/desenvolvimento/fluxo-de-trabalho.md`](_docs/desenvolvimento/fluxo-de-trabalho.md).

## Regras do mod

1. **Idioma:** o inglês é o idioma base. O que entra no jar, aparece no jogo ou acompanha o download do mod fica em inglês, como nome, IDs, comandos, config, logs e licença, e o `en_us.json` é a referência. Os arquivos de tradução, como o `pt_br.json`, podem ter textos em outros idiomas além do padrão, gerados pelo mesmo datagen e com as mesmas chaves do inglês, conferidas por um teste (perguntas 712 e 732).
2. **Sem magia:** nem nos nomes, nem nas mecânicas.
3. **Números em config:** bateria, consumo, raios, intervalos, dano e slots por tier nunca ficam fixos na lógica.
4. **Receitas:** os materiais são só do jogo puro, e uma receita pode combinar itens do próprio mod, como no conserto. Ficam em datapack JSON, para o pack poder sobrescrever com KubeJS.
5. **Energia:** FE, pelas capabilities do NeoForge, para aceitar energia de outros mods, quando houver. Tudo também aceita redstone, para funcionar sem outros mods.
6. **Luz:** API do LambDynamicLights, dependência obrigatória. Nada de blocos de luz invisíveis no mundo.
7. **Scanner de minérios:** baseado nas tags `c:ores`, nunca numa lista fixa de blocos.
8. **Estado do robô como item:** em data components do 1.21.1.
9. **Design próprio:** o modelo final não pode copiar um robô existente, para o mod poder ser publicado.
10. **Nome a evitar:** "Robit", que é o robô companheiro do Mekanism.

## Stack

| Item | Versão |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.1.x (o pack usa a 21.1.265) |
| Java | 21 |
| Build | MDK oficial do NeoForge para 1.21.1 (ModDevGradle) |
| GeckoLib | 4.9.3 (versão do pack) |
| LambDynamicLights | 4.8.11+1.21.1 (versão do pack, obrigatória, no cliente) |
| Jade | 15.10.6+neoforge (versão do pack, opcional) |
| JEI | 19.64.0.455 (versão do pack, opcional) |

Versões conferidas em 2026-11-04. Confira de novo no `mods/` do Iron Meridian antes de fixar ou atualizar uma dependência. O Gradle não está instalado na máquina: use o wrapper. O JDK 21 é baixado pelo próprio projeto.

## Como trabalhar

O desenvolvedor é sênior em TypeScript, Go e PHP, e Java é secundário para ele. Valoriza SOLID, DRY, KISS, Clean Code, TDD e DDD.

- **Modding:** explique as decisões específicas de Minecraft quando não forem óbvias.
- **Mudança neste arquivo:** justifique por que é necessária e espere a autorização do desenvolvedor.
- **Encerramento:** sessão que termina com trabalho pendente deixa um prompt de continuidade em `tmp/prompt/{AAAA-MM-DD_HHhMM}_continuidade_{assunto}.md`, com a leitura obrigatória, o que foi feito e o que se espera da próxima sessão.

### Decisões bloqueantes

É bloqueante qualquer ponto com mais de um caminho defensável em que a escolha altere arquitetura, dependências, processo, contrato, saves ou o comportamento entregue.

1. Identifique a decisão e pare antes de implementá-la.
2. Apresente as alternativas, com a sua recomendação e a opção de recusar todas.
3. Espere a confirmação explícita.
4. Implemente e registre a resposta em `_docs/perguntas/`, no arquivo da fase, e o resumo em `_docs/decisoes.md`.

Autorização para começar um trabalho não resolve os bloqueios dele, mesmo que a decisão pareça pequena ou reversível.

## Regras de desenvolvimento

### Prioridade

Quando duas regras puxarem para lados opostos, vence a que vem antes:

```text
Segurança > Defesa > Fronteira de módulo > Contrato externo > Comportamento testado >
Design sólido > Clareza > Simplicidade > Sem repetição > Sem excesso > Domínio > Regra do Escoteiro
```

O significado de cada pilar, os checklists e a matriz de decisão estão em [`_docs/desenvolvimento/pilares.md`](_docs/desenvolvimento/pilares.md). Segurança, aqui, é a proteção do mundo do player e da máquina dele.

### Domínio

11. **Conceito antes do código:** uma feature só é implementada depois de documentada em `_docs/funcionalidades/` e com as decisões dela fechadas.
12. **Fronteira:** `domain/` não importa `net.minecraft` nem `net.neoforged`. Um teste com ArchUnit reprova a violação no build. Regras de tier, bateria, consumo, filtros e decisão de comportamento ficam nessa camada.
13. **Energia é sempre inteira**, num value object. Transferir nunca cria nem perde FE, seja na bateria, na Power Bank ou na estação.
14. **Value objects** são `record` que validam no construtor. Sem `get*`: `capacity()`, não `getCapacity()`.
15. **Interfaces:** só existem com um consumidor real e são curtas. Se um consumidor não usa todos os métodos, separe.
16. **README do domínio:** `domain/` tem um `README.md` com a linguagem do domínio, as regras e os porquês. Atualizá-lo faz parte da tarefa que muda o modelo.

### Minecraft

17. **O mod não derruba o jogo por dado inválido:** usa um valor seguro e registra no log. Só falha ao iniciar, em caso de erro de programação.
18. **Saves são contrato a partir da `1.0.0`:** a partir dela, IDs, data components, NBT, chaves de config e tags não mudam sem migração. Até lá, podem mudar sem migração, com a decisão do desenvolvedor em cada caso e o que se perde nos mundos e no config dito antes (resposta 2189).
19. **Autoridade do servidor:** toda regra roda no servidor. O cliente só exibe e pede ações.
20. **Dono do robô:** toda ação confere o dono. O servidor valida dono e distância de todo pacote recebido do cliente.
21. **Orçamento de tick:** nada pesado a cada tick. Varreduras rodam em intervalos e sem alocação desnecessária.
22. **Chunks:** nunca carregue um chunk à força nem leia um bloco sem confirmar que ele está carregado. A exceção é o Servidor e as antenas da Fase 5, por ação do player, com limites no config.
23. **Separação do cliente:** código comum nunca referencia classes de `client/`.
24. **Dependências:** GeckoLib e LambDynamicLights são obrigatórias, e o mod precisa abrir sem Jade e sem JEI, que ficam isolados. Dependência só de cliente não pode impedir um servidor dedicado de abrir.

### Forma do código

25. **Idioma:** arquivos de programação (como `.java` e `.sh`) ficam em inglês, incluindo comentários, logs e testes. Arquivos de conteúdo (como `.md`) ficam em português, inclusive o nome do arquivo e da pasta, assim como issues, PRs e mensagens de commit. `README.md`, `AGENTS.md`, `CLAUDE.md`, `LICENSE` e `pull_request_template.md` mantêm o nome, porque as ferramentas dependem dele, e o `ROADMAP.md` segue o mesmo padrão dos arquivos da raiz. O `CHANGELOG.md` fica em inglês, no nome e no conteúdo, porque vai para as páginas de versão das plataformas (pergunta 713).
26. **Constantes em vez de literais:** nada de número mágico nem string literal solta, inclusive nos testes. Balanceamento vai no config, e o resto vira constante nomeada. O Checkstyle reprova a violação.
27. **Funções** têm no máximo 30 linhas e uma responsabilidade. Prefira classes pequenas e composição em vez de herança profunda.
28. **Nomes:** classes em `PascalCase`; JSONs e IDs em `snake_case`, no singular; scripts `.sh` e arquivos `.md` em `kebab-case`. Adapters levam o que adaptam como prefixo (`NeoForgeTierCatalog`).
29. **Comentários** são exceção e explicam o porquê. Sem código comentado e sem `TODO` sem issue.
30. **Gerados:** `src/generated/` nunca é editado à mão. Um gate reprova se o `runData` gerar diferença.
31. **Versões** ficam em `gradle.properties`, compatíveis com as do Iron Meridian.

### Testes

32. **TDD:** o teste vem antes ou junto do código. Use JUnit 5 no domínio e GameTests no que depende do mundo.
33. **Sem mock do jogo:** nunca faça mock de `Level` ou `Entity`. Comportamento de mundo se testa no mundo.
34. **Cobertura:** mínimo de 85% em `domain/`, medido com JaCoCo. O restante se cobre com GameTests e um roteiro manual.
35. **Teste antes da `main`:** teste numa instância só com o mod e suas dependências e, depois, numa do Iron Meridian.

### Gates

36. **Commit:** branch, histórico de modificações, formatação, mensagem e gitleaks já estão ativos. Veja [`_docs/desenvolvimento/hooks-do-git.md`](_docs/desenvolvimento/hooks-do-git.md).
37. **Push:** build, testes, ArchUnit, cobertura, formatação, Checkstyle e datagen já estão ativos. As GameTests saíram do push e rodam à mão antes de abrir um PR, pela regra 46 (pergunta 1711). Elas carregam o mod como servidor dedicado, o que também detecta classe de cliente carregada no servidor.
38. **Ferramentas:** toda ferramenta ou hook entra com um documento em `_docs/desenvolvimento/`: o que faz, por que foi escolhido, a versão e o que fazer quando reprova.

### Robô único

O Cubit é o único robô do mod. O que muda entre um Cubit e outro é o tier e o card de Aprendizado de Máquina, que dá a ele um trabalho. `general_robot` e `RobotType` continuam nos IDs, no config e no código.

39. **Números por dado:** regras, números e capacidades vêm do tier e do trabalho, nunca de um `if` ou de uma constante fixa.
40. **Capacidades por composição:** ímã, choque, scanner, inventário e os trabalhos são peças que o Cubit combina, sem herança.
41. **Config, IDs e dados:** chaves de config, IDs e data components seguem o padrão atual, com `general_robot`, porque, a partir da `1.0.0`, mudá-los exige migração (regra 18).
42. **Itens compartilhados:** Induction Dock, Cubit Tablet e Power Bank reconhecem o robô por tag ou interface, nunca por uma classe concreta.
43. **Sem antecipar trabalhos:** nada de implementar trabalhos, telas ou capacidades que ainda não entraram (regra 15).

### Validação

44. **Os documentos validam a entrega:** toda entrega é conferida contra os documentos `.md`, e as respostas em `_docs/perguntas/` prevalecem sobre os demais. Artefatos, como testes, logs, relatórios e capturas de tela, são bem-vindos na validação, mas não substituem os documentos.
45. **Validação no PR:** a descrição do PR informa os documentos conferidos e a evidência de cada um, incluindo os checklists dos pilares que se aplicam.

### Antes do PR

46. **Antes do PR:** antes de abrir um PR, o agente executa e analisa tudo o que está em [`_docs/desenvolvimento/antes-do-pr.md`](_docs/desenvolvimento/antes-do-pr.md) e põe na descrição as provas que o documento pede (pergunta 1715).

### Comandos do projeto

47. **Makefile:** os comandos do projeto ficam no `Makefile`. Receita com mais de 3 linhas vira um script `.sh` em `scripts/`, e o `Makefile` só o chama.

### Perguntas

48. **Toda pergunta respondida fica registrada:** qualquer pergunta feita ao desenvolvedor e respondida por ele entra em `_docs/perguntas/`, no arquivo da fase e com o próximo número livre, antes de seguir para o próximo passo. Vale também para pergunta não bloqueante e para resposta que só confirma a recomendação. Resposta que muda outra aponta para ela. A exceção é a autorização de uma ação pontual, como commit, push ou apagar uma branch, que vale só para aquela vez.

### Mudança no que está documentado

49. **O documentado pode mudar:** se um impedimento técnico aparecer ao avançar, a decisão pode ser alterada ou desfeita. O impedimento é mostrado com evidência, o desenvolvedor decide, e a nova resposta aponta para a antiga. A partir da `1.0.0`, os saves e o config seguem a regra 18.

### Independência

50. **Independência:** o mod prioriza funcionar numa instância de Minecraft só com ele e suas dependências. Nenhum recurso pode exigir outro mod ou modpack, e todo cenário é pensado primeiro assim. As exceções são GeckoLib e LambDynamicLights, obrigatórias, e Jade e JEI, opcionais.

### Início de feature

51. **Alinhamento:** ao iniciar uma feature, o agente descreve em poucas linhas o que vamos fazer e pergunta ao desenvolvedor se tem algo mais para acrescentar, antes da rodada de decisões, da issue e da branch.

### Rodadas de perguntas

52. **Nenhuma das opções:** toda pergunta com opções traz, de forma explícita, a opção "Nenhuma das opções". Vale para qualquer rodada, sem exceção.

### Depois do merge

53. **Encerramento do PR:** depois do merge de um PR, nesta ordem, o agente pede autorização para apagar a branch local e a remota, que não são mais necessárias, apresenta no que vai atuar em seguida e pergunta se o desenvolvedor quer algum ajuste no roteiro das próximas tarefas.

### Autorização do conceito

54. **Código só com o conceito autorizado:** para atuar no código, primeiro é preciso ter a autorização explícita do desenvolvedor sobre o conceito. Ao fechar o refinamento, o agente apresenta o conceito e espera essa autorização, que entra em `_docs/perguntas/` (regra 48).

### Apresentação

55. **Listas numeradas:** ao apresentar itens ao desenvolvedor, como opções, pendências, passos ou achados, prefira listas ordenadas numericamente, para ele poder citar cada item pelo número.

### Prompts de imagem

56. **Prompt em arquivo:** todo prompt de imagem fica em `tmp/arte/{nome}/prompt.md`, em que `{nome}` é o item ou o assunto, como `cascata`, com vários itens. Cada prompt traz o caminho completo da imagem que ele gera, na mesma pasta do `prompt.md`, como `tmp/arte/cascata/robot_core.png`, e o destino dela depois de aceita.

### GameTests

57. **GameTests só antes do PR:** as GameTests rodam só antes do envio de um PR, sobre o último commit da branch. Se alguma reprovar, o erro é ajustado, as GameTests rodam de novo, e o PR só abre depois que todas passarem. Fora desse momento, elas só rodam com autorização explícita do desenvolvedor. Rodá-las, ou ajustar o que elas mostrarem, não é motivo para parar o fluxo de trabalho (resposta 2085).

### XP

58. **XP que vira FE não volta:** o XP que se torna FE nunca volta a ser XP. Vale para tudo que trabalha com XP no mod, hoje e no que vier, como o Servidor e as profissões: nada no mod transforma FE em XP (resposta 2111).

### Prompts antes do código

59. **Recursos primeiro:** os prompts de geração de recursos, como imagens e sons, são prioridade e ficam prontos antes de iniciar o código da tarefa. Ao fechar o refinamento, o agente lista os recursos que a tarefa pede, ou diz que ela não pede nenhum, e escreve os prompts em `tmp/arte/{nome}/prompt.md`, como na regra 56 (respostas 2153 e 2154).

## Git

- **Autorização:** nunca faça commit, push, tag, release ou PR sem autorização explícita para aquela ação. Antes, mostre os arquivos, o resumo e a mensagem proposta.
- **Merge:** somente o desenvolvedor faz.
- **Autoria:** nunca atribua autoria ou coautoria a uma IA, nem em commits, PRs, tags, releases ou comentários.
- **Identidade:** os commits saem sempre pela conta `zenndg`, com o nome `Zennd` e o e-mail dela, no Git e no GitHub. Confira o `git config user.name` e o `user.email` antes de commitar (resposta 1482).
- **Assinatura:** os commits são assinados com PGP, com a chave da conta `zenndg`. Não desligue a assinatura (resposta 2128).
- **Remote:** `git@github.com-zenndg:zenndg/cubit.git`, da conta `zenndg`, para onde o repositório foi (respostas 1276 e 1278). Não troque por HTTPS nem por `git@github.com:`, que caem em outra conta.
- **Conta do `gh`:** a conta `zenndg`. Se o `gh api user --jq .login` mostrar outra, prefixe o comando com `GH_TOKEN=$(gh auth token --user zenndg)`.

### Fluxo de branches

O projeto segue o Gitflow, com Semantic Versioning e Conventional Commits.

| Branch | Sai de | Volta para | Papel |
|---|---|---|---|
| `main` | — | — | Versões publicadas; cada merge vira uma tag |
| `release/X.Y.Z` | `homolog` | `main` e `develop` | Release fechada |
| `homolog` | `develop` | `release/X.Y.Z` | Release em teste |
| `develop` | `main` | `homolog` | Integração do trabalho em andamento |
| `integration/{feature}` | `develop` | `develop` | Integração de uma feature grande em vários PRs, levada à `develop` num PR final |
| `feature/{issue}-{slug}` | `develop` ou `integration/{feature}` | A mesma de onde saiu | Funcionalidade, melhoria, correção não urgente ou tarefa técnica |
| `hotfix/{issue}-{slug}` | `main` | `main`, `homolog` e `develop` | Correção urgente do que já foi publicado |

### Regras de Git

- **Proibido:** commit direto em `main`, `homolog`, `develop` ou `integration/{feature}`; usar `--no-verify`; misturar várias mudanças lógicas num commit.
- **Issue:** toda tarefa começa por uma issue, e a branch leva o número dela. O nome da branch fica em inglês e em `kebab-case`: `feature/12-power-bank`.
- **PR:** todo merge é feito por PR, com `Closes #n` na descrição. A branch padrão no GitHub é a `develop`.
- **Commits:** em Conventional Commits, em português, e atômicos: `feat: adiciona a Power Bank`.
- **Versão:** Semantic Versioning, com tag anotada `vX.Y.Z` na `main`. Na fase `0.x`, mudança incompatível sobe o MINOR, e o resto sobe o PATCH.
- **Backlog:** um milestone do GitHub por fase do projeto.
- **Checklist:** ao encerrar um PR depois do merge, avalie se algum item de [`checklist.md`](checklist.md) deve ser marcado. A marcação entra num novo commit, no PR seguinte, nunca direto na `develop`.

## Estrutura de pacotes

Aprovada. Vale a partir da Fase 2.

```text
io.github.zenndg.cubit
├── CubitMod                // ponto de entrada: só conecta os registros
├── domain/                 // Java puro, testado com JUnit
│   ├── robot/              // RobotType, capacidades que o Cubit combina, OreKind, OreFilter, OreSignals, DefenseMode; PickupFilter, PickupEntry, PickupMode e PickupSettings: o filtro de coleta; TabletApp, RobotListing e RobotListings: os apps e a lista do Tablet; RobotName: o nome que o Tablet dá e o nome padrão; TabletApps: os apps que fazem sentido para o Cubit escolhido; TabletWheel: o lado da troca de app, também com a rolagem lateral do macOS; FollowCall: se o `Follow` do Tablet pode chamar o Cubit; WorkArea, AreaShape, AreaPath, AreaSide, AreaColumn, AreaLoop, AreaWays, AreaOutward, AreaBounds, AreaCorner e AreaCheck: a área desenhada, a forma, os cantos, o caminho por dentro e o lado de fora; AreaDrawing, AreaDrawingSpec, AreaMarking e AreaErase: o desenho, os números dele, a marcação e o apagar; UnloadChest, UnloadChests e UnloadCall: os baús de descarga e o `Descarregar`; RobotState também guarda a chave do local de morte; RobotJob: os trabalhos dos cards; JobSwitches: as chaves da aba Trabalho de cada trabalho; SpeedChoice e SpeedSteps: a velocidade escolhida e os degraus dela; OrganizerMode, OrganizerMark, OrganizerMarks e SupplyAmount: o modo, as marcas e os pedidos do Organizador; RoutePoint e PatrolRoute: os pontos da rota do Patrulheiro; RouteMarking: o que um clique do app Marcar faz na rota; IntroLink: o temporizador e o endereço do link da apresentação
│   ├── tier/               // RobotTier, TierSpec, TierCatalog (interface); TierUpgrade: subir de tier com um Module; ShockSpec: o choque e os modos de cada tier; TorchSpec: as tochas de cada tier; AreaSpec: a maior área de cada tier; UnloadSpec: os baús de cada tier; DeathPointLevel: o que cada tier faz com o local de morte; JobSpec: os trabalhos que cada tier aceita; FisherSpec: a Isca, a Sorte do Mar e o custo do Pescador em cada tier; FarmerSpec: o custo, a pausa, as plantas especiais, a farinha de osso e a Fortuna do Fazendeiro em cada tier; OrganizerSpec: os baús, as marcas, o custo, a pausa e a varredura do Organizador em cada tier; CookSpec: os fornos, o custo, a pausa e a varredura do Cozinheiro em cada tier; PatrollerSpec: as armas, a lentidão, a armadura, a procura, o salto, os pontos da rota e a área do Patrulheiro em cada tier; XpSpec: o tanque de XP do Cubit em cada tier
│   ├── energy/             // Battery, PowerState (ativo, inativo, hibernação), regras de consumo e religamento, SolarPanel: quando o painel dá energia; SolarStatus, EnergyUseCause, RechargeSource, EnergyFormat, EnergyHalves: o que as telas mostram; EnergyTransfer, WakeRecharge e PowerBankUpgrade: a Power Bank
│   ├── behavior/           // FollowDecision: seguir, parar ou teleportar; FollowStuck: quem segue e fica sem caminho; BurningAction: reagir a fogo e lava; LampPolicy: quando a lanterna acende; ScanRules, ScanArea, FreshFinds, MarkLife, MarkPulse, ScanSignal: scanners, caixas e cores; ChatWatch, BiomeWatch, LeftBehindWatch, ChatRules: falas; RespawnReturn: voltar com o dono; RobotScreenStatus: o estado no topo da tela; DefenseRules, ShockCandidate, RetaliationMemory, ShockRules: o choque e os modos de defesa; FetchRules e FetchMemory: a busca dos itens do chão; TorchRules, TorchGrid, GridCell e TorchPlanner: as tochas; ShockGaze: o olhar para o alvo do choque; DeathRecord, DeathLog e GameClock: o histórico de mortes e a hora do jogo; DeathGuideCall, DeathGuideSpec, DeathGuideProgress e DeathGuideStep: o botão e a guia até o local de morte; HibernationReason e AwayHibernation: a hibernação fora do Dock; WaterCell, OpenWater, FishingTimes, FishingPace, FishingSpot, FishingSpots, SpotReservations e JunkFilter: a água aberta, o tempo, o lugar e o lixo do Pescador; CropRipeness, TillRules e SeedChoice: a planta madura, o arar e a semente do Fazendeiro; ChestStock e StorageChoice: o baú onde o Organizador guarda; JobTalkTopic e JobTalkRules: as falas das profissões e quando saem; PatrolSettings, PatrolProgress, WeaponChoice, PatrolTargets, PatrolTraits, PatrolTeam, PatrolSiege, PatrolSpread, PatrolWatch, WingHop e PatrolRules: os números gerais, a ronda, a arma, o alvo, quem ele ataca e protege, a equipe, o cerco, a ronda espaçada, o vigia, o pulo de asa e as regras do Patrulheiro; PatrolStops: as paradas da ronda e para onde a cabeça olha; PatrolSound e PatrolGear: os sons das peças do Patrulheiro que mudam, decididos no RobotSoundRules; BladeGlow e Linger: quando a Plasma Blade fica acesa e o tempo em que ela e as asas continuam ligadas
│   ├── desk/               // DeskSpec e DeskCatalog (interface): os números da Engineering Desk por tier; DeskOrder: o pedido e o sinal do comparador; DeskOrderGate: o que o pedido precisa para sair de zero; CascadeRecipe, CascadeBook, CascadePlanner, CascadePlan e CascadeTask: o plano da fabricação em cascata; CascadeRun e CascadeTaskStatus: o plano em andamento; CascadeTable e CascadeLine: a tabela do plano; CascadeCost: a energia e o tempo dele; CascadeAmount e CascadeNeed: a quantidade pedida e o que o plano usa; CascadeCheck e CascadeStart: se o plano pode começar; MaterialLevel e CascadeChoice: a cor de um material e o item de uma marca com vários; DeskProgress: os ticks do craft; DeskCheck e DeskStatus: o que a mesa encontrou e o que ela faz, com o motivo da pausa; DeskStock: a energia da mesa; DeskLook: parada, criando ou em pausa, com a luz; DeskLid: a tampa do laptop; DrawerPace: o intervalo das gavetas; DeskCategory e CatalogSearch: as categorias e a busca do catálogo
│   ├── dock/               // DockStock, DockStatus, DockStay, DockQueue, DockTrip, DockRemoval, DockInputMeter; DockStorageRules e DockStoredKind: o inventário do Dock; DockGoal e PreferredDock: o `Ir para o Dock` e o Dock preferido do Tablet; AutoReturn: quando o Cubit volta sozinho ao Dock; DockSun e DockSunState: o que o painel vê, com o 3º bloco; DockPanel e DockPanelState: o painel solar e onde ele está; DockPanelSound: o som de cada momento do painel; DockDoorLight: a luz das portas; DockName: o nome do Dock; DockXpSpec: o tanque de XP e a passagem do Dock em cada tier; DockXpMode, DockXpRules e DockXpNotice: os modos do Dock, o que ele faz com o XP e o aviso do tanque cheio; XpUnloadCall: se o `Unload XP` manda o Cubit ao Dock
│   ├── xp/                 // XpRate, XpTank, XpMode, XpSettings e XpState: a taxa, o tanque, os modos e o que o Cubit guarda do XP; XpIntake: o XP do trabalho e das bolinhas; PlayerXp: o XP do player em pontos; XpFlow: o ritmo da passagem ao Dock e da conversão; XpConversion e XpEnergyTotal: o XP que vira energia, sem volta, e o total gerado; XpReturn: quando o Cubit cheio vai ao Dock; XpNotice: o aviso do tanque cheio
│   └── ownership/          // FollowingRobotPolicy, Ownership
├── config/                 // ModConfigSpec → implementa TierCatalog; DeathPointKey e DeathPointSection: o local de morte por tier e os números do histórico e da guia; JobKeys: o allowed_jobs de cada tier; FisherKeys e FisherSection: os números do Pescador por tier e os gerais, com a varredura do Fazendeiro; FarmerKeys: os números do Fazendeiro por tier; SpeedControlSection: o mínimo e o passo da velocidade; OrganizerKeys: os números do Organizador por tier; CookKeys: os números do Cozinheiro por tier; DeskKeys, DeskDefaults e EngineeringDeskSection: os números da Engineering Desk por tier e os gerais; JobDefaults: os trabalhos de cada tier e os números de cada card; JobChatterSection: as falas das profissões; PatrollerKeys, PatrollerWeaponKeys, PatrollerDefaults e PatrolSection: os números do Patrulheiro por tier e os gerais, com a distância e o tempo da lâmina acesa e o tempo das asas abertas; IntroSection: o temporizador e o endereço do link da apresentação, dentro da ChatterSection; NetworkSection: os números do desenho da área; XpSection e XpKey: os números gerais do XP, dentro da PickupSection, e o tanque por tier
├── registry/               // DeferredRegisters; CubitFluids: o fluido do XP; CubitDamageTypes, o dano do choque, do globo de laser e da Plasma Blade
├── entity/                 // RobotEntity, goals de IA; RobotNaming: o nome padrão e a janela do nome; RobotUpgrade: o Module aplicado; RobotDefense e RobotShock: o choque; RobotPickup: a coleta e o ímã; FetchItemGoal: a busca dos itens do chão; RobotTorches, TorchScan e RobotTorchSlot: as tochas; RobotGaze e RobotShockGaze: para onde a cabeça olha; RobotSnapshotReader: o Cubit lido para o Tablet; RobotTablet: abrir a tela e renomear pelo Tablet; RobotPreferredDock: o Dock preferido e o `Ir para o Dock`; RobotAutoReturn: o retorno automático e a chave dele; RobotFollowCall: o `Follow` do Tablet; RobotWorkArea e AreaWorkGoal: a área do Cubit e o trabalho nela; AreaWalk: o caminho por dentro da área; TrackJumpControl: as esteiras que não pulam; RobotUnload e UnloadGoal: os baús de descarga e a ida até eles; RobotDeathGuide e DeathGuideGoal: a chave, o texto ao renascer e a guia até o local de morte; DeathLogView: a lista da aba Mortes; RobotWork e RobotCardSlot: o card, o trabalho e a hibernação fora do Dock; RobotFishing, FishingGoal, FishingScan, FishingWater, PointsByDistance, FishingCatch, VirtualFishingHook e FishingEffects: o Pescador, a margem, a água, o sorteio na tabela do jogo e o respingo; RobotJobLook: o trabalho, a ferramenta na garra e a fase, o visor, a lâmina acesa, as asas em uso e a guarda do Patrulheiro que o cliente vê; RobotFarming, FarmingGoal, FarmingScan, FarmPlants, FarmSeeds, FarmWork, FarmAction e FarmTask: o Fazendeiro, a plantação, as sementes do baú e cada ação; RobotFertilizerSlot: o slot de fertilizante do Fazendeiro; RobotSpeed: a velocidade escolhida, aplicada a cada tick; RobotMarks: as marcas do app Marcar, de qualquer trabalho; JobErrands e ErrandGoal: os trabalhos feitos por tarefas e a caminhada até elas; RobotOrganizing, OrganizerTarget, OrganizerPlanner, OrganizerOrders, OrganizerChests, OrganizerStock e OrganizerTask: o Organizador, os pedidos, a escolha da tarefa, os baús e as transferências pela capability; RobotCooking, CookPlanner, CookTask e CookOvens: o Cozinheiro, os fornos, o cru e o combustível; RobotJobTalk: as falas das profissões; RobotPatrol, PatrolGoal e PatrolPause: a rota, a ronda e as paradas do Patrulheiro; RobotPatrolCombat, PatrolFightGoal, PatrolSight, PatrolTrail, PatrolReach e PatrolTeams: o combate, o alvo, o rastro do alvo que some, a medida da perseguição e a equipe; RobotEscort e EscortGoal: a escolta marcada e o seguir do escoltado; RobotPatrolWeapons, LaserOrb e PatrolIntercept: a pistola, o globo de laser, a Plasma Blade e a bola de fogo rebatida; RobotWings: o voo curto das asas sobre obstáculos e vãos, e o tempo em que elas continuam abertas; PatrolArmor, PatrolKills e PatrolProtection: a armadura extra, a cabeça, os drops e o XP no tanque, e os protegidos e os alvos, com as tags; RobotXp, RobotXpOrbs e RobotXpReturn: o tanque de XP e o trabalho, as bolinhas, e a ida ao Dock e a passagem; RobotDockReach e AreaReturn: se o Cubit alcança o Dock e a volta à área; RobotIntro: as frases de apresentação na primeira colocação, com a marca guardada na RobotChatter
├── item/                   // RobotItem, RobotStateComponent, ModuleItem e TierLook; InductionDockItem: o item do Dock, no GeckoLib; PowerBankItem, PowerBankUpgradeRecipe e RobotRechargeRecipe: a Power Bank; EngineeringRecipe: o tipo `cubit:engineering`, de todas as receitas do mod; EngineeringDeskItem e EngineeringDeskTooltip: o item da mesa, que guarda o estado e não se aninha com itens dentro; TabletItem: o Tablet; TabletAppLook: a cor e o texto de cada app; JobCardItem: os cards de Aprendizado de Máquina e a tag deles; SavedXp, SavedDockXp e XpTooltip: o XP no save do Cubit e do Dock e a linha dele nas dicas, no Jade e no Tablet; DeskCategorized e DeskCategoryCodecs: o campo `desk_category` das receitas da mesa
├── block/                  // InductionDockBlock, BlockEntity; InductionDockTopBlock e DockTop: o 3º bloco do Dock; DockSolarPanel: o painel, o 3º bloco e os sons; DockIdentity e DockOwner: o nome e o dono do Dock; DockAnimations: a pose e a transformação do Dock; XpFluid, DockXp, DockXpPipes e DockXpTelling: o fluido do XP, o XP do Dock, os canos e o aviso do tanque cheio; DockPresence: o Cubit na base e a fila; DockStorage e DockItemHandler: o inventário do Dock e a automação; StorableRobotItem: o Cubit e o Damaged Cubit que ele guarda; EngineeringDeskBlock, EngineeringDeskBlockEntity, DeskPart, DeskTier e DeskUpgrade: a Engineering Desk, as duas metades, o tier e o Module; DeskWork, DeskGrid, DeskMaterials, DeskOutput, DeskRecipes, DeskCatalogOrder e DeskCascade: o craft, as marcações, os materiais da entrada, a saída, as receitas, a ordem do catálogo e a cascata; DeskShortage e DeskUnlocks: o que falta na mesa e a liberação protegida das receitas; DeskJob, DeskInventories e DeskCraft: o pedido, os inventários e um craft da mesa; CascadeRecipes, CascadeLaunch, CascadeWork, CascadeState, CascadeStock e SavedCascade: as receitas, o início, o andamento, o estado, o estoque e o save da fabricação em cascata; DeskFaces, DeskEnergyStorage e DeskMaterialFilter: o que cada face aceita; DeskSaves e SavedDeskOrder: o save e o pedido no item; DeskFirstNotice: o texto da primeira mesa; DeskLookProperty: o estado de bloco `status`; DeskShow: a ligação, as gavetas, o pronto, quem está na tela e as atualizações dos clientes; DeskClientView: o progresso, a tela e o pronto no cliente; DeskClientTicks: o tick do cliente, ligado sem carregar `client/`; DeskAnimations: os três controladores de animação; OwnerName: o nome do dono de um Dock ou de uma mesa
├── menu/                   // RobotMenu, InductionDockMenu; DockSolarView: o que a aba Solar mostra; DefenseControl e DefenseOpening: a aba Defense; PickupControl e PickupMenuPart: a aba Behavior; TorchControl: os números das tochas; RobotMenuReading, RobotMenuContents e RobotMenuSource: as peças do menu do Cubit; TabletRobotMenu e TabletOpening: a tela do Cubit no Tablet; DockGoalControl: o `Ir para o Dock`; FollowControl: o `Follow`; UnloadControl: o `Descarregar`; DeathKeys e DeathPointControl: os textos e o tier do local de morte; WorkControl e WorkKeys: a aba Trabalho e o Retomar; SpeedControl e SpeedButtons: a velocidade e os botões − e +; OrganizerControl, OrganizerMenuPart, OrganizerScreenValues e SupplyRow: a linha Mode e a aba Supply; EngineeringDeskMenu, DeskControl, DeskScreenValues, DeskScreenKeys e DeskOpeners: a tela da mesa, os botões, os números, os textos e o que libera cada receita; CascadeMenuPart: a aba Cascade no menu; RobotMenuParts, XpMenuPart, XpControl e XpKeys: as partes do menu do Cubit e a aba XP; DockXpMenuPart e DockXpControl: a aba XP do Dock; DockOpening: o que viaja ao abrir a tela do Dock; IntHalves: um int em dois valores da tela
├── network/                // pacotes próprios: NoticePayload, OreMarksPayload, MobMarksPayload e CubitNetwork; RobotListPayload, ChooseRobotPayload e ChosenRobotPayload: a lista e a escolha do Tablet; RenameRobotPayload, RobotListRequestPayload, SwitchTabletAppPayload e TabletRequests: o que o Tablet pede ao servidor; AreaMarkPayload: a caixa do app Área; UnloadChestsPayload: as setas dos baús; DeathLogPayload, DeathGuidePayload e DeleteDeathPayload: a aba Mortes; OrganizerMarksPayload: as setas das marcas do app Marcar; PatrolRoutePayload: a rota do Patrulheiro escolhido; EscortMarksPayload: as setas sobre os escoltados; RenameDockPayload: o Rename do Dock; AskRobotNamePayload e RobotNamedPayload: a janela do nome do Cubit
├── compat/jade/            // plugin do Jade, opcional: só este pacote importa o Jade; DeskJadeProvider: a Engineering Desk no Jade; JadeTierLine: o tier na cor dele
├── compat/jei/             // plugin do JEI, opcional: só este pacote importa o JEI e, como o JEI só o carrega no cliente, pode usar as classes de cliente do jogo, nunca as de `client/` (resposta 1535); CubitJeiPlugin, DeskRecipeCategory, JeiDeskRecipes e DeskTransferHandler: as receitas da Engineering Desk e o botão +
├── player/                 // attachment com o UUID do robô que segue, robôs mortos com o dono offline, /cubit reset e o que o dono faz: renascer, conquistar, caçar e minerar redstone; TabletSelection, TabletEvents, RobotSnapshot, RobotSnapshots e ChosenRobotLink: a seleção do Tablet, a foto do Cubit descarregado e o Cubit escolhido; TabletUse, TabletTargetApp, TabletDockApp e TabletAreaApp: o clique de cada app; TabletAreaView: o que a linha do app Área mostra; TabletAreaErase e TabletAreaTexts: o apagar e os textos do app Área; TabletUnloadApp e TabletChestView: o app Descarga e as setas dos baús e das marcas; TabletMarkApp: o app Marcar; TabletEscortApp e TabletEscortView: o app Escort e as setas dos escoltados; TabletRouteApp e TabletRouteView: a rota do Patrulheiro no app Marcar e o que as setas mostram; OwnerDeaths, SavedDeathLog e TabletDeaths: o histórico de mortes, o save dele e os botões da aba Mortes; RobotScreenEvents: a tela do Cubit aberta e fechada, para a frase do card novo; PlayerXpTrade: a troca de XP do player com um tanque
├── client/                 // renderer, modelo GeckoLib, telas, com as abas Inventory, Settings, Defense e Behavior; TabletSelectionScreen e TabletArrow: a seleção e a seta do Tablet; TabletRobotScreen, TabletRobotHeader, TabletResourcesPanel, TabletRenameDialog, TabletOfflineScreen e TabletHeaderButtons: a tela do Cubit no Tablet; TabletScroll: a troca de app com Shift e a roda; TabletAreaMarks e TabletAreaRenderer: a linha do app Área; AreaLineBars e AreaLinePainter: as barras e a pintura dela; TabletAppStrip: a faixa com a cor do app no ícone; TabletChestMarks: as setas sobre os baús marcados; TabletRouteMarks: as setas numeradas e a linha da rota do Patrulheiro; TabletEscortMarks: as setas douradas sobre os escoltados; TabletDeathLog e TabletDeathsPanel: a aba Mortes; RobotWorkPanel: a aba Trabalho, na tela do Cubit e no Tablet; RobotJobGearLayer, RobotToolLayer e RobotJobIconLayer: o chapéu e a mochila de cada trabalho, as peças do Patrulheiro com o brilho das que estão em uso, a vara ou a enxada e o ícone na tela; LaserOrbRenderer: o globo de laser desenhado; SpeedRow: a linha da velocidade, na aba Settings e na aba Recursos do Tablet; OrganizerModeRow e SupplyPanel: a linha Mode e a aba Supply do Organizador; EngineeringDeskScreen, DeskCatalog, DeskGridView, DeskOrderView, DeskTab, DeskEnergyBar e DeskScreenFrames: a tela da mesa, com o motivo do + travado; ProjectTab: a aba Projeto; DeskCatalogRules, ProjectCatalogRules e CascadeCatalogRules: a regra do catálogo em cada aba; CascadeTab, CascadeReading, CascadeMaterials, CascadeTableView e CascadeQuestion: a aba Cascade, com o que ela lê, os materiais, a tabela e o aviso do Iniciar; ScrollRows e CheckMark: a rolagem das linhas e o visto; DeskRenderer, DeskItemRenderer, DeskItemExtensions, DeskGeoModel: o modelo GeckoLib da mesa, no mundo e no item; TierBand: a faixa do tier, da mesa e do Dock; DockRenderer, DockGeoModel, DockItemRenderer e DockItemExtensions: o modelo GeckoLib do Dock, no mundo e no item; DockScreenLayer: a tela de status do Dock; DockPanelGlow: o brilho das células do painel; DockMoonlight: se o painel gera pela lua, para o brilho e a tela do Dock; DockLights: as luzes das portas; DockRename: o Rename do Dock; RobotNameScreen: a janela do nome do Cubit; DeskGlow: as cores da tela do laptop e da luminária; DeskHologram e TintedBuffers: o holograma do item da receita; DeskLoopSound e DeskSoundLoops: o loop do som de criando; BlueprintPainter, BlueprintButton, DeskCategoryBar e DeskScreenHeader: a tela de prancheta da mesa, os ícones das categorias e o cabeçalho; XpPanel, XpTankView, XpFluidLook e ScissorBox: a aba XP, o tanque, as texturas do fluido e o corte do desenho; XpTradeRow, XpModeRows, XpExcessRow, XpHeading e XpTips: as partes das abas XP; DockXpPanel e TabletXpPanel: a aba XP do Dock e a do Tablet; DockMainTab: a aba Dock
└── datagen/                // receitas, loot, traduções; CubitCardRecipes: as receitas dos cards e da carcaça deles; SharedPartRecipes: as receitas das peças compartilhadas; CubitItemTagsProvider: a tag dos cards; EngineeringRecipeOutput e DeskRecipeData: as receitas no tipo da mesa e a receita dela; DeskTexts e PortugueseDeskTexts: os textos da mesa; CascadeTexts e PortugueseCascadeTexts: os textos da aba Cascade; DeskCategoryData: a categoria de cada receita da mesa; JobTalkPhrases e PortugueseJobTalkPhrases: as frases das profissões; PatrollerTexts e PortuguesePatrollerTexts: os textos do Patrulheiro; XpTexts e PortugueseXpTexts: os textos do XP; CubitFluidTagsProvider: o fluido na tag de experiência; CubitDamageTypeTagsProvider: o globo como projétil, na tag de dano do jogo
```

Os testes ficam em `src/test` (JUnit) e os GameTests em um pacote `gametest`.

## Notas de modding

- **Limite do domínio:** um teste com ArchUnit deve falhar se `domain` importar `net.minecraft` ou `net.neoforged`. É mais simples que um subprojeto Gradle.
- **Cliente e comum:** `Menu` roda nos dois lados, e `Screen` só no cliente. Código comum não pode carregar classes de `client`, senão um servidor dedicado quebra.
- **Rede:** o menu e o `SynchedEntityData` bastam para o estado do robô. Os textos usam um pacote próprio, só do servidor para o cliente, e os pacotes do cliente para o servidor só entram com o Tablet, que substitui o controle remoto, sempre conferidos no servidor (regra 20).
- **Config `COMMON`:** fica em `config/`, onde o packwiz consegue distribuí-lo. O `SERVER` ficaria dentro de cada mundo.
- **Atributos:** são registrados antes de o config carregar, então vida e velocidade são aplicadas quando a entidade nasce.
- **Receitas com datagen:** geram JSONs normais em `data/`, que o KubeJS pode sobrescrever.
- **Distribuição:** o pack usa packwiz e não guarda `.jar`, então o mod precisa ser publicado no Modrinth ou no CurseForge para entrar nele.
