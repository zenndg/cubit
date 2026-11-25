# Modo área

Números: [`tiers.md`](../tiers.md#autonomia) e [valores gerais](../tiers.md#valores-gerais).

Os comandos e o localizar do antigo controle remoto passaram para o [Tablet](tablet.md), que substitui o Cubit Remote (perguntas 998 a 1002).

## Modo área

O modo área entra com o item 13 da Fase 4.2, numa entrega só, depois do retorno automático do item 15 (perguntas 1057 e 1069). A descarga no baú marcado, o app Descarga e o botão `Descarregar` entram com o item 14, em [`inventario.md`](inventario.md#descarregar-em-baú).

### Marcar a área

A área é desenhada com o app Área do [Tablet](tablet.md#apps) (perguntas 2240 a 2246, 2311 e 2350 a 2361):

1. **O Cubit:** o clique num Cubit do player, no mundo, faz dele o Cubit escolhido. Com um Cubit Base, que não tem modo área, ou de outro player, a barra de ação diz o motivo. Clicar num bloco antes de escolher o Cubit só avisa. O clique no mesmo Cubit mantém o desenho, e em outro Cubit do player, recomeça com ele (pergunta 2357).
2. **Os pontos:** cada clique num bloco marca um ponto, de perto ou na mira, a até 32 blocos. As linhas só fazem ângulo reto, e o desenho vai de 4 a 32 pontos.
3. **Fechar:** o clique no primeiro ponto fecha a forma, e a linha passa de vermelha a verde. Com ela verde, o clique num ponto só tira o fechamento, e os pontos ficam (pergunta 2355).
4. **Aplicar:** com a linha verde, Shift + clique dá a área ao Cubit, e ele vai para ela. Com a vermelha, Shift + clique desfaz o último ponto, também mirando num Cubit (perguntas 2245 e 2352).

- **Forma:** os blocos dos pontos e das linhas fazem parte da área, e a altura vai do ponto mais baixo ao mais alto (perguntas 2244 e 2311).
- **Limites:** os de [`tiers.md`](../tiers.md#autonomia), 16×16, 32×32 e 64×64, valem para o retângulo em volta da forma, e a altura máxima, 8, 24 e 32 blocos, fica no config, por tier (perguntas 342, 1050, 1601 e 2350).
- **Recusas no clique:** fora do ângulo reto, sobre o próprio desenho, pontos demais, grande ou alto demais para o tier, e fechar com menos de 4 pontos. O ponto não entra, a barra de ação diz o motivo, e o desenho continua. Linhas lado a lado valem (perguntas 2354 e 2356).
- **Alcance:** ao aplicar, a área precisa estar na dimensão do Cubit, com o centro do retângulo em volta dela dentro do raio de chamada do Dock do tier, `call_radius_blocks`. Fora disso, a barra de ação diz o motivo, e o desenho continua (perguntas 1058 e 2351).
- **Linha:** só com o Tablet na mão e o app Área. É uma barra de 3 px do bloco, com volume, e um cubo de 6 px em cada ponto. O trecho até a mira é translúcido e só aparece quando o clique entraria, com o bloco na mira marcado. Desenhando, é um traço só, que sobe na vertical. Fechada ou aplicada, mostra o contorno da base e o do topo. Aplicada, fica verde e sem os cubos. Atrás de um bloco, fica mais fraca (perguntas 2358 a 2361).
- **Desenho pela metade:** fica só na memória do servidor, por player, e some ao trocar de app, ao mudar de dimensão ou ao sair do jogo. Uma nova área substitui a anterior do Cubit (perguntas 1060 e 2245).
- **Apagar:** sem desenho em andamento, Shift + clique no Cubit pede confirmação, e o segundo clique em 5 s apaga a área. O Cubit para e desliga, com a carga mantida, e o clique direito o liga. No Dock, continua no Dock (perguntas 2299, 2352 e 2353).
- **Áreas antigas:** as áreas em caixa, de antes da `0.2.0`, são apagadas ao carregar o Cubit, e o resto dele não muda (pergunta 2283).

### O Cubit na área

- **Chegada:** ele deixa de seguir e vai andando. Preso sem progresso, com qualquer card, teleporta para dentro da área (perguntas 2092 e 1058).
- **Limite:** ele só sai da área para ir ao Dock, ao baú marcado ou quando não há outro caminho. Numa forma como o L, contorna o canto por dentro, e o que está no corte fica fora da área (pergunta 2365).
- **Sem card:** com a chave `Pickup` ligada, busca os itens do chão dentro da área, respeitando o filtro de coleta. A chave `Fetch` vale só para quem segue o dono, e, com `Pickup` desligada, o Cubit só faz a ronda (perguntas 340 e 1080). O ímã continua puxando pelo raio do tier, mesmo de fora dela (pergunta 1064).
- **Ronda:** sem item para buscar, ele dá voltas contínuas pelas bordas da área, de canto a canto, também pelos cantos de dentro de um L, na altura em que anda, dentro do volume, gastando com andar. Um canto que ele não alcança é pulado. Um item para buscar interrompe a ronda (perguntas 1059 e 1062).
- **Volume:** a área é a forma desenhada, do ponto mais baixo ao mais alto. Conta como dentro dela também o bloco acima do topo, onde os itens ficam e o Cubit fica em pé sobre os blocos marcados, então pontos no chão já fazem uma área plana que funciona.
- **Defesa, tochas e lanterna:** seguem como hoje (pergunta 1059).
- **Inventário cheio:** com a chave `Unload` ligada e um baú marcado com espaço, vai descarregar e volta para a área ([descarga](inventario.md#descarregar-em-baú)). Sem isso, para de coletar e fica na área, e o estado mostra o inventário cheio, tentando descarregar de novo a cada intervalo (perguntas 1052, 1087 e 1088).
- **Bateria baixa:** pelo [retorno automático](energia.md#retorno-automático), que entra antes, vai ao Dock preferido, ou ao livre mais próximo no raio do tier, e com a carga cheia volta à área. Sem Dock, segue na área até o crítico e desliga, como seguindo (perguntas 1053, 1065 e 1068).
- **Dono longe:** o Cubit nunca teleporta até o dono e não é desligado pela perda de vínculo (pergunta 326). Com o chunk descarregado, para como o jogo para tudo e continua quando o chunk carregar, sem forçar o carregamento (pergunta 1061).
- **Estado:** `In area` (`Na área`), com cor própria, no topo da tela e na lista do Tablet. A bateria crítica e o inventário cheio aparecem antes dele (pergunta 1063).
- **Tela:** a aba Recursos do Tablet mostra a área, com os cantos e o tamanho do retângulo em volta dela.
- **Voltar a seguir:** o botão `Follow`, no cabeçalho da tela do Cubit no Tablet, que já entra com o retorno automático, também apaga a área e faz o Cubit voltar a seguir (perguntas 1055 e 1072).
- **Ao recolher:** a área, os baús marcados e o Dock preferido se perdem (perguntas 343 e 1008).
- **Com trabalho, na Fase 5:** vale [`trabalhos.md`](trabalhos.md).

## Com a Network

Planejado, na `0.2.0`, e ainda fora do código. As regras estão em [`network.md`](network.md). O desenho por pontos já entrou. Falta ele ficar dentro dos chunks das antenas do Cubit, com o tamanho saindo do tier. A altura continua a do tier. Sem rede, o Cubit não vai à área (perguntas 2206, 2225 e 2350).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-25 00:23 | Descreve a área desenhada, entregue na #264 (perguntas 2240 a 2246 e 2350 a 2365). |
| Zennd | 2026-11-25 00:02 | Corrige a altura da área do Elite para 24 blocos (perguntas 1601 e 2349). |
| Zennd | 2026-11-24 22:42 | Aponta para a Network (pergunta 2325). |
| Zennd | 2026-11-24 08:37 | Estende a volta à área a todos os cards (pergunta 2092). |
| Marcelo Fabiano | 2026-11-14 13:55 | Liga o inventário cheio à descarga (perguntas 1087 e 1088). |
| Marcelo Fabiano | 2026-11-14 12:10 | Define as chaves da busca na área (pergunta 1080). |
| Marcelo Fabiano | 2026-11-14 11:20 | Liga o `Follow` da área ao botão do #181 (pergunta 1072). |
| Marcelo Fabiano | 2026-11-14 10:00 | Liga a bateria baixa ao retorno automático (perguntas 1065 e 1069). |
| Marcelo Fabiano | 2026-11-14 09:00 | Define a marcação, a caixa e o Cubit na área (perguntas 1049 a 1064). |
| Marcelo Fabiano | 2026-11-13 16:40 | Vira o modo área, e os comandos e o localizar passam para o Tablet. |
| Marcelo Fabiano | 2026-11-13 09:05 | Deixa só as tochas entre as funções que o controle desativa. |
| Marcelo Fabiano | 2026-11-07 23:42 | Acrescenta os scanners ao consumo do Cubit parado. |
| Marcelo Fabiano | 2026-11-07 16:51 | Cita a marcação de um mob no modo só o alvo. |
| Marcelo Fabiano | 2026-11-06 13:34 | Adiciona a chave e o consumo da lanterna. |
| Marcelo Fabiano | 2026-11-06 11:10 | Troca 'especialidade' por 'trabalho' e 'card de especialidade' por 'card de Aprendizado de Máquina'. |
| Marcelo Fabiano | 2026-11-05 21:34 | Define o parar, a área em 3D e o que o Cubit sem card faz nela. |
| Marcelo Fabiano | 2026-11-05 00:12 | Usa o termo modo de defesa. |
| Marcelo Fabiano | 2026-11-04 17:28 | Define o alcance do controle e da função de localizar. |
| Marcelo Fabiano | 2026-11-04 17:15 | Renomeia para português e atualiza os caminhos. |
| Marcelo Fabiano | 2026-11-04 17:11 | Adiciona a função de localizar e o alcance do controle. |
| Marcelo Fabiano | 2026-11-04 17:08 | Define as funções que o controle remoto desativa. |
| Marcelo Fabiano | 2026-11-04 16:57 | Adiciona o comportamento ao desligar pelo controle. |
| Marcelo Fabiano | 2026-11-04 15:23 | Descreve as features do Cubit. |
