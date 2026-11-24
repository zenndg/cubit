# Access Transformer

O arquivo [`accesstransformer.cfg`](../../src/main/resources/META-INF/accesstransformer.cfg) abre campos privados do jogo que o mod precisa ler, sem dependência nova (pergunta 2068).

## O que faz

É um recurso do próprio NeoForge: ao montar o ambiente de desenvolvimento e ao abrir o jogo, ele troca o acesso das classes e dos campos listados. Cada linha traz o acesso, a classe e o campo.

| Campo | Para quê |
|---|---|
| `AbstractFurnaceBlockEntity.recipesUsed` | O XP guardado na fornalha, que o Cozinheiro toma e zera, para não sair duas vezes (pergunta 1824) |
| `ExperienceOrb.followingPlayer` | O player que a bolinha segue, para o Cubit deixar a de outro player (pergunta 1933) |
| `ExperienceOrb.count` | Quantas bolinhas ela junta, para nenhum ponto se perder nem duplicar (perguntas 1822 e 2065) |

## Por que foi escolhido

- **NBT e recálculo:** salvar e reler a fornalha e a bolinha seria mais lento e frágil, e o player seguido só poderia ser deduzido.
- **Mixin:** mais invasivo do que o caso pede.

## Versão

A do NeoForge, em `gradle.properties`. O ModDevGradle acha o arquivo sozinho, em `src/main/resources/META-INF/accesstransformer.cfg`.

## Regras

- Só entra um campo que o código lê, com o motivo num comentário do próprio arquivo, em inglês (regra 25).
- Nunca se abre uma classe inteira.

## O que fazer quando reprova

- **O build não acha o campo:** o nome mudou no Minecraft ou no NeoForge. Confira o nome no jar de `build/moddev/artifacts/` e ajuste a linha.
- **Erro de acesso no jogo:** confira se o arquivo está no jar, com `unzip -l build/libs/*.jar`.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-24 08:16 | Documenta o Access Transformer (pergunta 2068). |
