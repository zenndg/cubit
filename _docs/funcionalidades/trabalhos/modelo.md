# Modelo de trabalho

Modelo para padronizar o `.md` de cada trabalho desta pasta. Copie o bloco abaixo para `{nome-do-trabalho}.md`, em `kebab-case` e em português, e preencha cada campo. Os quatro campos do cabeçalho são obrigatórios. As seções do corpo entram quando o trabalho tiver conteúdo para elas.

## Status

Todo trabalho tem um, e só um, destes status:

| Status | Quando usar |
|---|---|
| Ideia | O conceito existe, mas as decisões ainda não foram fechadas |
| Refinado | As decisões foram fechadas com o desenvolvedor. Antes de virar issue, elas precisam estar registradas em `_docs/perguntas/` |
| Implementado | O código está pronto e testado numa branch, mas ainda não foi publicado |
| Entregue | Saiu numa versão publicada do mod |
| Descartado | Foi abandonado ou substituído por outro trabalho. A seção "Por que foi descartado" diz o motivo e, se houver, o trabalho que o substitui |

Quem muda o status atualiza o campo e adiciona uma linha no histórico.

## Bloco para copiar

```markdown
# {Nome do trabalho}

**Conceito:** {uma ou duas frases: o que o Cubit faz com este card e por que o player o quer}

**Criado em:** {AAAA-MM-DD HH:MM}

**Status:** {Ideia | Refinado | Implementado | Entregue | Descartado}

Ele complementa [`trabalhos.md`](../trabalhos.md), cujas regras de toda área valem aqui. O documentado pode mudar se aparecer um impedimento técnico.

## A ideia

{O problema do player que o trabalho resolve e como o Cubit o resolve.}

## Card

| Nome | ID | Tiers | Receita |
|---|---|---|---|
| Machine Learning: {Nome em inglês} | `cubit:ml_{id}` | {tiers} | {receita} |

## Fluxo

1. {Passo a passo do player, do card no slot até o Cubit trabalhando.}

## Regras

- **{Regra}:** {descrição, com a pergunta que a decidiu}.

## Evolução por tier

Os números ficam em [`tiers.md`](../../tiers.md#trabalhos), todos no config.

| Recurso | {Tier} | {Tier} |
|---|---|---|
| {recurso} | {valor} | {valor} |

## Visual e personalidade

- **Peça:** {o que identifica o trabalho no modelo do Cubit}.
- **Tela:** {o que muda na tela do Cubit}.
- **Falas:** {o tom e alguns exemplos}.

## Em aberto

1. **{Ponto}.** Recomendação: {a recomendação atual}.

## Por que foi descartado

{Só para o status Descartado: o motivo e o trabalho que o substitui.}

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | {AAAA-MM-DD HH:MM} | Cria o conceito do {Nome do trabalho}. |
```

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-22 16:46 | Cria o modelo de trabalho, com o título, o conceito, a data de criação e o status. |
