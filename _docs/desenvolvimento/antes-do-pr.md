# Antes do PR

O que se executa e o que se analisa antes de abrir um PR, e a prova que cada item deixa na descrição dele. A regra 46 do [`AGENTS.md`](../../AGENTS.md) manda aplicar este documento a todo PR (perguntas 1711 e 1715).

## Quando

- **Momento:** depois do último commit da branch e antes do `gh pr create`, com a branch atualizada com a de origem, como no passo 9 de [`fluxo-de-trabalho.md`](fluxo-de-trabalho.md#9-pull-request).
- **Sobre o quê:** sempre sobre o último commit da branch. Se um commit novo entrar depois, o que ele afeta roda de novo, e a prova passa a citar o commit novo.
- **Por que aqui:** as GameTests levam de 5 a 10 minutos e saíram do `pre-push` (pergunta 1711). Este é o ponto em que elas são obrigatórias.

## O que se executa

1. **GameTests:** `make gametest`, obrigatório quando a tarefa muda o jar (código, assets, dados ou config). Uma GameTest reprovada impede o PR: o erro é ajustado, elas rodam de novo, e o PR só abre com todas aprovadas, sem parar o fluxo de trabalho. Fora deste momento, elas só rodam com autorização explícita do desenvolvedor (regra 57 e pergunta 2085). Numa tarefa só de documentos, não se aplica. Depois de uma rodada aprovada, ela só se repete se mudar código de teste ou do mod. Um commit só de documentos não pede outra, e a prova cita o commit em que elas rodaram (pergunta 2004).
2. **Build completo:** `./gradlew check --rerun-tasks`, que compila e roda os testes JUnit, o ArchUnit, a cobertura de 85% do domínio, o Spotless e o Checkstyle, sem o cache do Gradle.
3. **Datagen:** `rm -rf src/generated/resources/.cache` e `make check-generated`. Sem o cache, um arquivo gerado restaurado também é conferido.
4. **Cobertura das GameTests:** `make gametest-coverage`, opcional, quando a tarefa muda bastante o código fora do domínio.
5. **Teste manual:** o passo 7 de [`fluxo-de-trabalho.md`](fluxo-de-trabalho.md#7-teste-manual), numa instância só com o mod, quando a tarefa muda o jar. Antes da `main`, também no Iron Meridian (regra 35).

## O que se analisa

1. **Documentos:** a entrega conferida contra os `.md` que a descrevem, com as respostas de [`perguntas/`](../perguntas/README.md) prevalecendo (regra 44).
2. **Respostas:** toda pergunta respondida na tarefa está registrada, e o que ela decidiu está nos documentos (regra 48).
3. **Pilares:** os checklists de [`pilares.md`](pilares.md) que se aplicam (regra 45).
4. **Domínio:** o `README.md` de `domain/` está atualizado, se o modelo mudou (regra 16).
5. **Estrutura de pacotes:** a lista do `AGENTS.md` cita as classes que entraram e não cita as que saíram.
6. **Números e config:** uma chave nova ou mudada está em [`tiers.md`](../tiers.md) e em [`config.md`](config.md).
7. **Checklist:** se algum item de [`checklist.md`](../../checklist.md) será marcado depois do merge, no PR seguinte.

## As provas no PR

As provas ficam na seção Validação do [modelo de PR](../../.github/pull_request_template.md). Os logs ficam em `tmp/log/`, que o Git ignora, então o PR leva o trecho com o resultado, não o arquivo.

| Item | Prova na descrição |
|---|---|
| GameTests | A linha `All N required tests passed`, com o número, e o commit curto em que rodou |
| Build completo | `BUILD SUCCESSFUL`, com a duração, e o commit curto |
| Datagen | `make check-generated` sem diferença, com o cache apagado, e o commit curto |
| Cobertura das GameTests | A cobertura total do relatório, ou "não rodou", com o motivo |
| Teste manual | Aprovado pelo desenvolvedor, com a data, ou "não se aplica", com o motivo |
| Documentos | A tabela Documento e Evidência do modelo |
| Pilares | Os checklists marcados e os que não se aplicam, com o motivo |

- **Só o resultado final:** uma falha corrigida durante a tarefa não precisa de prova. Vale o resultado no último commit.
- **Não se aplica:** um item que não se aplica fica na descrição com o motivo, e nunca em branco.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-24 08:11 | Cita a regra das GameTests só antes do PR (pergunta 2085). |
| Zennd | 2026-11-23 13:08 | Repete as GameTests só quando muda código de teste ou do mod (pergunta 2004). |
| Zennd | 2026-11-22 16:30 | Cria o documento do que vem antes do PR e das provas na descrição (perguntas 1711 e 1715). |
