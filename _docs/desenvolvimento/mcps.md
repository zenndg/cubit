# MCPs

Os servidores MCP dão ferramentas a mais ao agente que trabalha no projeto, como o Claude Code. São só de desenvolvimento: nada deles entra no jar, no build ou nos gates, e o projeto compila e é testado sem nenhum (perguntas 2379 a 2383 e 2386).

## O que cada um faz

| MCP | O que faz | Versão | Onde roda |
|---|---|---|---|
| `blockbench` | Modela, texturiza e anima no Blockbench aberto | Plugin MCP 1.10.1, no Blockbench 5.2.2, com o plugin do GeckoLib 4.2.5 | Na máquina, em `http://127.0.0.1:3000/bb-mcp` |
| `bbmodel` | O mesmo, sem janela, sobre arquivos `.bbmodel` | O plugin MCP 1.10.1, pelo Bun 1.4.2 | Na máquina, restrito a `~/workspace/blockbench-models` |
| `serena` | Acha, lê e refatora o código Java por símbolo | 2.1.0 | Na máquina, com o projeto na pasta `.serena/` |
| `minecraft-modding` | Fontes e mapeamentos do Minecraft | `@adhisang/minecraft-modding-mcp` 8.2.0 | Na máquina, baixado pelo npm |
| `context7` | Documentação de bibliotecas | Serviço, sem versão fixa | Fora da máquina: cada consulta é enviada a ele |

## Por que foram escolhidos

São os que o desenvolvedor já usava em outro agente nesta máquina, com as mesmas versões. O `mcp.js` instalado é idêntico ao publicado pelo [projeto dele](https://github.com/jasonjgardner/blockbench-mcp-plugin), conferido por SHA-256 em 2026-11-25. O Dock e as prévias da Network foram feitos sem eles, por script.

## Instalação

Cada um é registrado no Claude Code com `claude mcp add`, no escopo `local`, que vale só para o usuário da máquina e para a pasta em que o comando roda. Rode na raiz do projeto. Não existe `.mcp.json` no repositório. As ferramentas só carregam numa sessão nova, e o `/mcp` mostra o estado.

## Regras

- **Geometria:** a fonte da geometria é o script que a gera e mede. O Blockbench confere, texturiza e anima.
- **Arquivos gerados:** nada em `src/generated/` é editado por um MCP (regra 30).
- **Código e jogo:** o que um MCP diz do Minecraft é conferido nos fontes do NeoForge 21.1.265, em `build/moddev/artifacts/`, que são a referência.
- **Dados para fora:** ao `context7` só vão perguntas sobre bibliotecas, nunca código, nomes ou conteúdo do projeto.
- **Autoria:** nenhum MCP grava autoria de IA em commit, PR, tag, release ou comentário.
- **`.serena/`:** fica no `.gitignore` (pergunta 2381). As memórias dele não são documentação do projeto.

### Opções do plugin no Blockbench

Ficam em Settings > General, e as duas vêm ligadas de fábrica:

| Opção | O que faz | Situação |
|---|---|---|
| Enable risky_eval | Executa qualquer JavaScript que um cliente MCP enviar, com as permissões do plugin | Desligado pelo desenvolvedor (pergunta 2383), conferido no servidor em 2026-11-25 |
| Disclose AI Usage | Grava no `.bbmodel` quais clientes de IA o editaram | Desligado pelo desenvolvedor (pergunta 2386), conferido nas opções gravadas do Blockbench em 2026-11-25 |

O `bbmodel` grava a mesma marca por conta própria, e a opção do Blockbench não o alcança: ele só para com `--no-ai-disclosure` no registro. O formato do GeckoLib, que vai ao jar, é escrito pelos scripts e não leva a marca.

O servidor só aceita conexões da própria máquina. O `MCP Server Host` fica em `localhost`. Para conferir o `risky_eval`, peça a lista ao `get_capabilities`, com `include_tools`: ele deve aparecer com `enabled: false`. Sem projeto aberto, a maioria das ferramentas também aparece assim, e o `risky_eval` depende só da opção.

## O que fazer quando um falha

- **`blockbench` com `ECONNREFUSED`:** o Blockbench está fechado ou o plugin não carregou. Abra-o e confira a porta 3000.
- **`bbmodel` não acha o arquivo:** ele só lê e grava na pasta dele. Copie o modelo para lá.
- **`minecraft-modding` não sobe:** confira o Node 24 e o Java 21 no `PATH` do registro, com `claude mcp get minecraft-modding`.
- **Um MCP sumiu:** o registro foi feito em outra pasta. Rode `claude mcp list` na raiz do projeto.
- **`serena` com símbolo velho:** apague `.serena/cache`, que ele refaz.
- **Nenhum funciona:** siga sem eles, com os scripts, os fontes do jogo e o Gradle.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-25 11:23 | Confere o `Disclose AI Usage` desligado no Blockbench (pergunta 2386). |
| Zennd | 2026-11-25 11:10 | Explica que o `bbmodel` grava a marca de IA por conta própria (pergunta 2386). |
| Zennd | 2026-11-25 10:43 | Decide desligar o `Disclose AI Usage` (pergunta 2386). |
| Zennd | 2026-11-25 10:37 | Confere o `risky_eval` e diz como conferir (pergunta 2383). |
| Zennd | 2026-11-25 10:14 | Registra o `risky_eval` desligado (pergunta 2383). |
| Zennd | 2026-11-25 10:12 | Documenta os cinco MCPs (perguntas 2379 a 2382). |
