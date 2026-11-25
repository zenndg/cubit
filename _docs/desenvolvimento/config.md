# Config

O config do Cubit é um contrato com o pack (pilar Contrato externo): a partir da `1.0.0`, as chaves abaixo não mudam sem migração (regra 18 do `AGENTS.md`).

- **Arquivo:** `config/cubit-common.toml`, do tipo `COMMON`, para o packwiz poder distribuí-lo.
- **Valores padrão:** são os de [`tiers.md`](../tiers.md), a única página com os números de balanceamento.
- **Crescimento:** uma chave entra junto com o código que a lê. Os tiers Advanced, Elite e Ultimate entram na Fase 4, com as mesmas chaves do Base nas seções `[general_robot.advanced]`, `[general_robot.elite]` e `[general_robot.ultimate]`, e os padrões de [`tiers.md`](../tiers.md) (perguntas 39 e 797).

## Chaves

As chaves ficam em `snake_case`. As de um tier ficam em `[general_robot.{tier}]`, como `[general_robot.base]` (regra 41 do `AGENTS.md`). A tabela mostra as do Base, e os outros tiers repetem as mesmas chaves e limites.

| Seção | Chave | Linha em `tiers.md` | Limites |
|---|---|---|---|
| `general` | `initial_charge_percent` | Carga do Cubit ao sair do craft | 0 a 100 |
| `general` | `wake_charge_percent` | Religar | 0 a 100 |
| `general` | `critical_charge_percent` | Bateria crítica, com expressão de sono, indicador vermelho e texto | 0 a 100 |
| `general` | `notice_repeat_ticks` | Intervalo mínimo entre repetições de textos e alertas | 1 a 72000 |
| `general` | `chatter_repeat_ticks` | Intervalo mínimo entre falas sobre o tempo | 1 a 72000 |
| `general` | `chatter_left_behind_blocks` | Distância para reclamar de ficar para trás | 1 a 128 |
| `general` | `chatter_left_behind_repeat_ticks` | Intervalo mínimo entre falas de ficar para trás | 1 a 72000 |
| `general` | `chatter_biome_repeat_ticks` | Intervalo mínimo entre falas de bioma | 1 a 72000 |
| `general` | `chatter_animal_repeat_ticks` | Intervalo mínimo entre falas de animais | 1 a 72000 |
| `general` | `chatter_animal_chance_percent` | Chance de falar ao abater um animal | 0 a 100 |
| `general` | `chatter_redstone_repeat_ticks` | Intervalo mínimo entre falas da redstone | 1 a 72000 |
| `general` | `chatter_check_ticks` | Intervalo para ler o tempo e o horário das falas | 1 a 1200 |
| `general` | `rare_ore_tags` | Minérios raros, do mais raro para o menos raro | Lista de tags |
| `general` | `surprised_expression_ticks` | Tempo com a expressão de surpresa | 1 a 1200 |
| `general` | `scan_mark_ticks` | Tempo da caixa em volta de um minério | 1 a 1200 |
| `general` | `scan_blocks_per_tick` | Limite de blocos lidos por tick numa varredura | 1 a 2146689 |
| `general` | `screen_light_level` | Luz da claridade da tela | 0 a 15 |
| `general` | `lamp_on_below_light` | A lanterna acende abaixo da luz | 0 a 15 |
| `general` | `lamp_off_from_light` | A lanterna apaga a partir da luz | 0 a 15 |
| `general` | `lamp_check_ticks` | Intervalo para ler a luz do ambiente | 1 a 1200 |
| `general` | `solar_check_ticks` | Intervalo para ler se o sol chega ao painel | 1 a 1200 |
| `general` | `happy_expression_ticks` | Tempo com a expressão feliz | 1 a 1200 |
| `general` | `hurt_expression_ticks` | Tempo com a expressão triste, depois de um dano | 1 a 1200 |
| `general` | `worried_expression_ticks` | Tempo com a expressão de preocupado | 1 a 1200 |
| `general` | `worried_radius_blocks` | Distância para ficar preocupado com o dono | 1 a 128 |
| `general` | `dying_expression_ticks` | Tempo com a expressão de quase morto, depois de um dano | 1 a 1200 |
| `general` | `dying_health_percent` | Vida para a expressão de quase morto | 0 a 100 |
| `general` | `base_movement_speed` | Velocidade 1,0× | 0,01 a 1 |
| `general` | `follow_repath_ticks` | Intervalo para recalcular a rota | 1 a 200 |
| `general` | `stop_distance_blocks` | Distância para parar perto do player | 1 a 16 |
| `general` | `max_drop_blocks` | Altura máxima que a rota desce | 1 a 16 |
| `general` | `teleport_distance_blocks` | Distância para teleportar até o player | 17 a 128 |
| `general` | `burning_speed_multiplier` | Velocidade em chamas, sobre a do tier | 1 a 4 |
| `general` | `full_inventory_speed_multiplier` | Velocidade com o inventário cheio, sobre a do tier | 0,1 a 1 |
| `general` | `water_search_radius_blocks` | Busca de água em chamas | 1 a 16 |
| `general` | `burning_wander_radius_blocks` | Alcance ao andar ao acaso em chamas e ao sair da lava ou de um bloco que fere | 1 a 16 |
| `general` | `dock_input_per_tick` | Entrada do Induction Dock | 1 a 2147483647 |
| `general` | `dock_full_heal_ticks` | Tempo para recuperar toda a vida na base | 1 a 72000 |
| `general` | `dock_stuck_ticks` | Tempo sem avançar antes de desistir do Dock, e de aparecer na área ou perto do dono | 1 a 1200 |
| `general` | `dock_unreachable_ticks` | Tempo em que o Cubit deixa de fora o Dock de que desistiu (pergunta 2095) | 1 a 72000 |
| `general` | `dock_complete_hold_ticks` | Tempo com a tela completa antes de sair da base | 1 a 1200 |
| `general` | `dock_solar_deploy_delay_ticks` | Tempo seguido com sol para o Dock montar o painel, e sem sol para guardá-lo (perguntas 1856 e 1912) | 0 a 1200 |
| `general` | `redstone_dust_energy` | Energia de 1 pó de redstone | 1 a 2147483647 |
| `general` | `redstone_block_energy` | Energia de 1 bloco de redstone | 1 a 2147483647 |
| `general` | `recharge_splash_health_percent` | Recharge Splash: vida e energia que dá, a parte da vida | 0 a 100 |
| `general` | `recharge_splash_energy_percent` | Recharge Splash: vida e energia que dá, a parte da energia | 0 a 100 |
| `general` | `recharge_splash_radius_blocks` | Recharge Splash: raio a partir do impacto | 1 a 16 |
| `general` | `recharge_splash_duration_ticks` | Recharge Splash: tempo em que a vida e a energia entram, e a espera até a próxima | 1 a 1200 |
| `general` | `repair_health_percent` | Vida ao sair do conserto | 1 a 100 |
| `general` | `void_rescue_retry_ticks` | Intervalo para tentar de novo o resgate no vazio | 1 a 1200 |
| `general` | `retaliate_memory_ticks` | Tempo em que o Contra-atacar lembra de quem feriu o Cubit ou o dono | 1 a 72000 |
| `general` | `focus_forget_distance_blocks` | Distância do dono em que o alvo do Focus deixa de ser alvo | 1 a 128 |
| `general` | `tablet_target_reach_blocks` | Alcance da mira do app Alvo do Tablet | 1 a 128 |
| `general` | `shock_look_after_ticks` | Tempo em que o Cubit ainda olha para o ponto do alvo depois de ele morrer ou se perder | 1 a 1200 |
| `general` | `xp_fluid_per_point` | Fluido, em mB, de 1 ponto de XP (pergunta 1817) | 1 a 1000 |
| `general` | `xp_jobs` | Trabalhos que dão XP ao tanque. Um nome desconhecido é ignorado, com um aviso no log (pergunta 1820) | Nomes dos trabalhos |
| `general` | `farmer_xp_chance` | Fazendeiro: chance de 1 ponto por colheita (pergunta 1823) | 0 a 100 |
| `general` | `dock_fe_per_xp_point` | Dock: energia, em FE, de 1 ponto de XP, sem volta (perguntas 1942 e 2111) | 1 a 1000 |
| `general` | `dock_xp_notice_blocks` | Dock: distância até o dono do aviso de tanque de XP cheio (pergunta 2103) | 0 a 128 |
| `general` | `pickup_check_ticks` | Intervalo da coleta por contato | 1 a 1200 |
| `general` | `magnet_check_ticks` | Intervalo do ímã | 1 a 1200 |
| `general` | `torch_check_ticks` | Intervalo da análise das tochas | 1 a 1200 |
| `general` | `torch_scan_radius_blocks` | Raio em que o Cubit procura lugares perigosos | 1 a 16 |
| `general` | `torch_reach_blocks` | Distância até onde o Cubit põe uma tocha | 1 a 8 |
| `general` | `fetch_check_ticks` | Intervalo da procura de um item para buscar | 1 a 1200 |
| `general` | `fetch_give_up_ticks` | Tempo até desistir de um item que não alcança | 1 a 1200 |
| `general` | `fetch_ignore_ticks` | Tempo em que o item desistido é ignorado | 1 a 1200 |
| `general_robot.base` | `battery_capacity` | Bateria | 1 a 2147483647 |
| `general_robot.base` | `dock_energy_capacity` | Estoque do Induction Dock desse tier. Troca a chave geral de mesmo nome (pergunta 1381) | 1 a 2147483647 |
| `general_robot.base` | `dock_recharge_per_tick` | Recarga do Cubit e da Power Bank guardada no Dock desse tier | 1 a 2147483647 |
| `general_robot.base` | `dock_storage_slots` | Slots do inventário do Dock desse tier | 9 a 54, em múltiplos de 9 |
| `general_robot.base` | `dock_solar_per_tick` | Geração do painel do Dock desse tier, com o painel montado (perguntas 1385 e 1855) | 0 a 2147483647 |
| `general_robot.base` | `xp_tank_capacity` | Tanque de XP do Cubit desse tier, em mB (pergunta 1826) | 1 a 2147483647 |
| `general_robot.base` | `dock_xp_tank_capacity` | Tanque de XP do Dock desse tier, em mB (pergunta 1833) | 1 a 2147483647 |
| `general_robot.base` | `dock_xp_transfer_per_tick` | Fluido que o Cubit passa ao Dock desse tier, em mB por tick (pergunta 1835) | 1 a 2147483647 |
| `general_robot.base` | `dock_solar_night_per_tick` | Geração do painel do Dock desse tier à noite, com 0 para não gerar (pergunta 1944) | 0 a 2147483647 |
| `general_robot.base` | `moving_consumption_per_tick` | Andar | 0 a 2147483647 |
| `general_robot.base` | `full_inventory_extra_per_tick` | Extra ao andar com o inventário cheio | 0 a 2147483647 |
| `general_robot.base` | `water_extra_per_tick` | Extra ao andar na água | 0 a 2147483647 |
| `general_robot.base` | `negative_condition_extra_per_tick` | Extra com condição negativa | 0 a 2147483647 |
| `general_robot.base` | `climb_extra_per_block` | Extra ao subir, por bloco de altura | 0 a 2147483647 |
| `general_robot.base` | `lamp_consumption_per_tick` | Extra com a lanterna acesa | 0 a 2147483647 |
| `general_robot.base` | `lamp_light_level` | Lanterna | 0 a 15 |
| `general_robot.base` | `solar_day_per_tick` | Painel solar (de dia) | 0 a 2147483647 |
| `general_robot.base` | `solar_night_per_tick` | Painel solar (à noite) | 0 a 2147483647 |
| `general_robot.base` | `ore_scan_radius_blocks` | Scanner de minérios (raio) | 1 a 64 |
| `general_robot.base` | `mob_scan_radius_blocks` | Scanner de mobs hostis (raio) | 1 a 64 |
| `general_robot.base` | `ore_scan_interval_ticks` | Intervalo de varredura de minérios | 1 a 72000 |
| `general_robot.base` | `mob_scan_interval_ticks` | Intervalo de varredura de mobs | 1 a 72000 |
| `general_robot.base` | `ore_scan_cost` | Scanner de minérios, por varredura | 0 a 2147483647 |
| `general_robot.base` | `mob_scan_cost` | Scanner de mobs, por varredura | 0 a 2147483647 |
| `general_robot.base` | `ore_filter_slots` | Minérios no filtro do scanner | 1 a 64 |
| `general_robot.base` | `recharge_per_tick` | Recarga na base e pelo slot de energia | 1 a 2147483647 |
| `general_robot.base` | `inventory_slots` | Inventário | 1 a 54 |
| `general_robot.base` | `max_health` | Vida | 1 a 1024 |
| `general_robot.base` | `call_radius_blocks` | Raio do retorno automático e da chamada pelo Dock | 1 a 128 |
| `general_robot.base` | `return_charge_percent` | Bateria do retorno automático | 0 a 100 |
| `general_robot.base` | `unload_chests` | Baús marcados para descarregar | 0 a 16 |
| `general_robot.base` | `unload_reach_blocks` | Distância máxima até o baú | 1 a 128 |
| `general_robot.base` | `area_size_blocks` | Modo área, lado da base, com 0 para bloqueado | 0 a 128 |
| `general_robot.base` | `area_height_blocks` | Altura máxima da área, com 0 para bloqueado | 0 a 64 |
| `general_robot.base` | `speed_multiplier` | Velocidade | 0,1 a 4 |
| `general_robot.base` | `defense_modes` | Modos de defesa: `peaceful`, `retaliate`, `aggressive` e `focus` | Lista com esses nomes |
| `general_robot.base` | `shock_damage` | Choque (dano) | 0 a 1024 |
| `general_robot.base` | `shock_range_blocks` | Choque (alcance) | 0 a 16 |
| `general_robot.base` | `shock_interval_ticks` | Choque (intervalo) | 1 a 72000 |
| `general_robot.base` | `shock_cost` | Custo do choque | 0 a 2147483647 |
| `general_robot.base` | `shock_hits_all_in_range` | Alvos por choque: todos no alcance, ou só o mais próximo | `true` ou `false` |
| `general_robot.base` | `magnet_radius_blocks` | Ímã (raio), com 0 para bloqueado | 0 a 16 |
| `general_robot.base` | `magnet_cost_per_item` | Ímã, por item puxado | 0 a 2147483647 |
| `general_robot.base` | `torch_cost` | Colocar tochas, por tocha | 0 a 2147483647 |
| `general_robot.base` | `torch_light_level` | Tochas: luz de bloco mínima que o Cubit garante, com 0 para bloqueado | 0 a 15 |
| `general_robot.base` | `fetch_radius_blocks` | Busca (raio em volta do dono), com 0 para bloqueado | 0 a 16 |
| `general_robot.base` | `pickup_filter_slots` | Filtro de coleta | 1 a 27 |
| `general_robot.base` | `pickup_filter_tags` | Filtro de coleta com tags | `true` ou `false` |
| `general_robot.base` | `power_bank_capacity` | Power Bank (70% da bateria) | 1 a 2147483647 |
| `general_robot.base` | `death_point` | Local de morte: `locked`, `mark` ou `guide`. Um valor inválido vale como `locked` e vai para o log | `locked`, `mark` ou `guide` |
| `general_robot.base` | `engineering_desk_energy_capacity` | Engineering Desk: energia que a mesa guarda | 1 a 2147483647 |
| `general_robot.base` | `engineering_desk_craft_ticks` | Engineering Desk: tempo de cada craft | 1 a 72000 |
| `general_robot.base` | `engineering_desk_storage_slots` | Engineering Desk: slots da entrada | 1 a 36 |
| `general_robot.base` | `engineering_desk_max_order` | Engineering Desk: maior pedido | 1 a 4096 |
| `general_robot.base` | `engineering_desk_cascade` | Engineering Desk: se a mesa desse tier fabrica em cascata (pergunta 2020) | `true` ou `false` |
| `general_robot.base` | `allowed_jobs` | Cards de Aprendizado de Máquina que o tier aceita: `fisher`, `farmer`, `rancher`, `organizer`, `cook`, `patroller`, `miner`, `builder`, `mechanic` e `alchemist`. Um nome inválido é ignorado e vai para o log | Lista com esses nomes |
| `general_robot.base` | `fisher_lure_level` | Pescador: Isca | 0 a 3 |
| `general_robot.base` | `fisher_luck_level` | Pescador: Sorte do Mar | 0 a 3 |
| `general_robot.base` | `fisher_catch_cost` | Pescador: custo por peixe pescado | 0 a 2147483647 |
| `general_robot.base` | `farmer_action_cost` | Fazendeiro: custo por planta colhida ou bloco arado | 0 a 2147483647 |
| `general_robot.base` | `farmer_action_pause_ticks` | Fazendeiro: pausa entre ações | 1 a 72000 |
| `general_robot.base` | `farmer_special_crops` | Fazendeiro: plantas de colheita especial | `true` ou `false` |
| `general_robot.base` | `farmer_bone_meal` | Fazendeiro: slot de fertilizante | `true` ou `false` |
| `general_robot.base` | `farmer_fortune_level` | Fazendeiro: Fortuna | 0 a 3 |
| `general_robot.base` | `organizer_chests` | Organizador: baús marcados | 0 a 64 |
| `general_robot.base` | `organizer_marks` | Organizador: máquinas ou destinos marcados | 0 a 64 |
| `general_robot.base` | `organizer_action_cost` | Organizador: custo por pilha movida | 0 a 2147483647 |
| `general_robot.base` | `organizer_action_pause_ticks` | Organizador: pausa entre ações | 1 a 72000 |
| `general_robot.base` | `organizer_scan_interval_ticks` | Organizador: intervalo da varredura | 1 a 72000 |
| `general_robot.base` | `cook_ovens` | Cozinheiro: fornos marcados | 0 a 64 |
| `general_robot.base` | `cook_action_cost` | Cozinheiro: custo por pilha movida | 0 a 2147483647 |
| `general_robot.base` | `cook_action_pause_ticks` | Cozinheiro: pausa entre ações | 1 a 72000 |
| `general_robot.base` | `cook_scan_interval_ticks` | Cozinheiro: intervalo da varredura | 1 a 72000 |
| `general_robot.base` | `patroller_pistol_damage` | Patrulheiro: dano da pistola | 0 a 100 |
| `general_robot.base` | `patroller_pistol_range` | Patrulheiro: alcance da pistola | 1 a 64 |
| `general_robot.base` | `patroller_pistol_interval_ticks` | Patrulheiro: intervalo da pistola | 1 a 1200 |
| `general_robot.base` | `patroller_pistol_cost` | Patrulheiro: custo por disparo | 0 a 2147483647 |
| `general_robot.base` | `patroller_slowness_level` | Patrulheiro: nível da lentidão do globo | 1 a 5 |
| `general_robot.base` | `patroller_blade_damage` | Patrulheiro: dano da lâmina | 0 a 100 |
| `general_robot.base` | `patroller_blade_interval_ticks` | Patrulheiro: intervalo da lâmina | 1 a 1200 |
| `general_robot.base` | `patroller_blade_knockback` | Patrulheiro: nível do empurrão da lâmina | 0 a 5 |
| `general_robot.base` | `patroller_blade_reach` | Patrulheiro: alcance do golpe da lâmina | 1 a 6 |
| `general_robot.base` | `patroller_armor` | Patrulheiro: armadura extra | 0 a 30 |
| `general_robot.base` | `patroller_search_radius` | Patrulheiro: raio de procura | 1 a 64 |
| `general_robot.base` | `patroller_jump_height` | Patrulheiro: altura do salto e do pulo de asa | 0 a 64 |
| `general_robot.base` | `patroller_jump_cost` | Patrulheiro: custo do salto | 0 a 2147483647 |
| `general_robot.base` | `patroller_gap_distance` | Patrulheiro: maior vão que atravessa planando | 0 a 16 |
| `general_robot.base` | `patroller_route_points` | Patrulheiro: pontos da rota | 0 a 128 |
| `general_robot.base` | `patroller_area_size` | Patrulheiro: lado da área, com a altura do tier e 0 para bloqueado | 0 a 256 |
| `general_robot.base` | `patroller_return_charge_percent` | Patrulheiro: bateria do retorno automático, no lugar de `return_charge_percent` enquanto o card estiver no slot | 0 a 100 |
| `general` | `death_log_size` | Mortes no histórico do player | 1 a 50 |
| `general` | `death_guide_arrival_blocks` | Distância de chegada da guia ao local de morte | 1 a 16 |
| `general` | `death_guide_lead_blocks` | Quanto o Cubit anda à frente do dono na guia | 2 a 16 |
| `general` | `death_guide_stuck_seconds` | Tempo sem avançar antes de cancelar a guia | 1 a 60 |
| `general` | `speed_control_min_percent` | Velocidade: o menor valor dos botões, sobre a velocidade do tier | 10 a 100 |
| `general` | `speed_control_step_percent` | Velocidade: o passo dos botões − e + | 1 a 50 |
| `general` | `door_open_cost` | Custo de abrir uma porta marcada | 0 a 10000 |
| `general` | `door_marks_limit` | Portas marcadas por dono | 1 a 256 |
| `general` | `engineering_desk_craft_energy` | Engineering Desk: custo de cada craft | 0 a 2147483647 |
| `general` | `engineering_desk_input_per_tick` | Engineering Desk: energia que a mesa recebe por tick | 1 a 2147483647 |
| `general` | `unload_interval_default_minutes` | Intervalo de descarga na área, padrão de um Cubit novo, com 0 para só quando cheio | 0 a 1440 |
| `general` | `unload_interval_max_minutes` | Intervalo de descarga na área, o maior que a aba Trabalho oferece | 1 a 1440 |
| `general` | `farmer_scan_interval_ticks` | Fazendeiro: intervalo da varredura da plantação | 1 a 72000 |
| `general` | `fisher_wait_min_ticks` e `fisher_wait_max_ticks` | Pescador: espera antes da mordida | 1 a 72000 |
| `general` | `fisher_bite_min_ticks` e `fisher_bite_max_ticks` | Pescador: mordida | 1 a 72000 |
| `general` | `fisher_lure_ticks_per_level` | Pescador: espera a menos por nível de Isca | 0 a 72000 |
| `general` | `fisher_cast_reach_blocks` | Pescador: alcance do lançamento | 1 a 16 |
| `general` | `patroller_slowness_chance` | Patrulheiro: chance de lentidão no acerto da pistola | 0 a 100 |
| `general` | `patroller_slowness_ticks` | Patrulheiro: duração da lentidão | 1 a 72000 |
| `general` | `patroller_head_chance` | Patrulheiro: chance de cabeça ao matar com a lâmina | 0 a 100 |
| `general` | `patroller_chase_distance` | Patrulheiro: perseguição máxima | 0 a 64 |
| `general` | `patroller_weapon_switch_distance` | Patrulheiro: distância da troca de arma | 0 a 64 |
| `general` | `patroller_patrol_speed` | Patrulheiro: velocidade da ronda, sobre a normal | 0,1 a 1 |
| `general` | `patroller_stop_min_ticks` e `patroller_stop_max_ticks` | Patrulheiro: parada na ronda ou na investigação | 0 a 72000 |
| `general` | `patroller_stop_min_distance` e `patroller_stop_max_distance` | Patrulheiro: blocos andados até a próxima parada | 1 a 256 |
| `general` | `patroller_alert_ticks` | Patrulheiro: alerta ao avistar um hostil | 0 a 1200 |
| `general` | `patroller_owner_check_ticks` | Patrulheiro: olhar para o dono depois do combate | 0 a 1200 |
| `general` | `patroller_owner_check_distance` | Patrulheiro: distância do dono para olhar para ele | 1 a 64 |
| `general` | `patroller_visor_calm_ticks` | Patrulheiro: tempo sem ameaça até o visor subir | 0 a 1200 |
| `general` | `patroller_lost_sight_ticks` | Patrulheiro: tempo sem ver o alvo até ele sumir de vista, com 0 para na hora | 0 a 1200 |
| `general` | `patroller_intercept_interval_ticks` | Patrulheiro: intervalo da procura de uma bola de fogo | 1 a 20 |
| `general` | `patroller_intercept_guard_distance` | Patrulheiro: distância até o protegido a quem a bola de fogo se dirige | 1 a 64 |
| `general` | `patroller_glide_drop_blocks` | Patrulheiro: maior queda que o caminho planeja, descendo planando | 5 a 64 |
| `general` | `patroller_escort_still_ticks` | Patrulheiro: tempo do escoltado parado até a parada da escolta | 1 a 200 |
| `general` | `patroller_blade_ignite_distance` | Patrulheiro: distância do alvo em que a Plasma Blade acende, mesmo enquanto ele atira | 0 a 64 |
| `general` | `patroller_wings_linger_ticks` | Patrulheiro: tempo em que as asas continuam abertas e grandes depois do pouso ou do fim do voo | 0 a 1200 |
| `general` | `patroller_blade_linger_ticks` | Patrulheiro: tempo em que a Plasma Blade continua acesa depois do último momento em que devia estar acesa | 0 a 1200 |
| `general` | `patroller_creeper_blast` | Patrulheiro: raio da explosão do creeper | 1 a 16 |
| `general` | `patroller_keep_away` | Patrulheiro: distância que mantém do creeper sem carga | 1 a 32 |
| `general` | `patroller_search_interval_ticks` | Patrulheiro: intervalo da procura de alvos | 1 a 200 |
| `general` | `patroller_blade_knockback_base` e `patroller_blade_knockback_step` | Patrulheiro: força do empurrão e o acréscimo por nível | 0 a 5 |
| `general` | `patroller_call_range` | Patrulheiro: alcance do chamado aos outros Patrulheiros | 1 a 128 |
| `general` | `patroller_wing_detour_factor` | Patrulheiro: quantas vezes o caminho a pé pode passar da linha reta antes do pulo de asa | 1 a 10 |
| `general` | `patroller_low_health_percent` | Patrulheiro: vida abaixo da qual ele luta só com a pistola, com 0 para nunca | 0 a 100 |
| `general` | `patroller_rescue_radius` | Patrulheiro: raio em volta do colega com a vida baixa em que a equipe o socorre | 1 a 32 |
| `general` | `job_chatter_chance` | Falas das profissões: chance num evento do trabalho | 0 a 100 |
| `general` | `job_chatter_interval_ticks` | Falas das profissões: intervalo mínimo entre duas falas do mesmo Cubit | 1 a 72000 |
| `general` | `job_chatter_range` | Falas das profissões: distância do dono para ouvir | 1 a 128 |
| `general` | `intro_link_cooldown_ticks` | Apresentação: tempo, por player, sem repetir a frase do link | 0 a 72000 |
| `general` | `intro_link_url` | Apresentação: endereço do link, só `https://` | Texto |
| `network` | `area_min_points` | Desenho da área, o mínimo de pontos | 4 a 32 |
| `network` | `area_max_points` | Desenho da área, o máximo de pontos | 4 a 256 |
| `network` | `area_reach_blocks` | Desenho da área, o alcance da mira | 1 a 128 |
| `network` | `chunk_loading_enabled` | Carregamento de chunks do mod | Ligado ou desligado |
| `network` | `release_when_owner_offline` | Solta os chunks com o dono offline | Ligado ou desligado |
| `network` | `chunks_per_player` e `chunks_per_world` | Chunks por player e no mundo, com 0 para sem limite | 0 a 2147483647 |
| `network` | `chunk_cost` | Chunk, por tick | 0 a 2147483647 |
| `network` | `chunk_resume_energy` | Carga com que o Servidor zerado volta a segurar o chunk | 0 a 2147483647 |
| `network` | `server_ups_percent` | UPS padrão do Servidor | 0 a 50 |
| `network` | `relay_energy_capacity` e `relay_input_per_tick` | Solar Relay: bateria e entrada | 1 a 2147483647 |
| `network` | `storage_slot_limit` | Itens por slot do Servidor | 1 a 999 |
| `network.{tier}` | `energy_capacity` | Network Server, a bateria | 1 a 2147483647 |
| `network.{tier}` | `channels` | Network Server, os canais | 1 a 16 |
| `network.{tier}` | `input_per_tick` | Network Server, a entrada por cabo e redstone | 1 a 2147483647 |
| `network.{tier}` | `storage_slots` | Network Server, os slots | 9 a 54 |
| `network.{tier}` | `relay_solar_per_tick` e `relay_solar_night_per_tick` | Solar Relay, de dia e à noite | 0 a 2147483647 |

## Por que esses limites

- **Energia até 2147483647:** é o maior valor de um `int`, o tipo usado pelas capabilities de FE do NeoForge.
- **Inventário até 54:** é o tamanho do baú duplo, o maior que a tela padrão do jogo mostra.
- **Vida até 1024:** é o máximo do atributo de vida do jogo.
- **Raio até 128:** mantém a busca limitada (regra 21 do `AGENTS.md`).
- **Raio dos scanners até 64:** cobre os 20 blocos de minérios e os 25 de mobs do Ultimate com folga, sem deixar a varredura crescer sem limite. Em 64 blocos, são até 2.146.689 blocos por varredura, o limite de `scan_blocks_per_tick`.
- **Luz até 15:** é o maior nível de luz do jogo.
- **Ímã até 16 blocos:** cobre os 8 blocos do Ultimate com folga, e 0 deixa o ímã bloqueado no tier, como no Base (pergunta 889).
- **Filtro de coleta até 27:** é a grade de 3 linhas de 9 slots fantasma da aba Behavior, o tamanho do Ultimate.
- **Parada e teleporte sem sobreposição:** os limites não se cruzam, então o config nunca faz o robô parar além da distância em que teleportaria.

## Fora do config

A margem de 4 blocos para abrir uma tela é a constante do próprio jogo, `Container.DEFAULT_DISTANCE_BUFFER`, porque não é balanceamento (pergunta 335).

## Valor inválido

O mod não cai por causa do config (regra 17 do `AGENTS.md`):

| Situação | O que acontece |
|---|---|
| `area_max_points` menor que `area_min_points` | O mínimo vale também como máximo, para o desenho ainda fechar |
| `server_ups_percent` fora dos passos de 5 | Vale o passo de baixo |
| `chunk_resume_energy` menor que `chunk_cost` | Vale o custo, para o chunk não piscar |
| `storage_slot_limit` abaixo da pilha normal do item | O slot guarda a pilha normal |
| `storage_slots` da rede que não fecha uma linha de 9 | Vale a linha de baixo, e o mod registra no log |
| Número fora dos limites | O NeoForge usa o limite mais próximo e registra no log |
| Chave ausente ou valor que não é número | O NeoForge volta ao padrão da chave e registra no log |
| `lamp_off_from_light` menor que `lamp_on_below_light` | A lanterna apaga a partir do maior dos dois, e o mod registra no log uma vez |
| Tag inválida em `rare_ore_tags` | O mod ignora a tag e registra no log uma vez |
| Nome inválido em `defense_modes` | O mod ignora o nome e registra no log uma vez. O Pacífico vale sempre, mesmo fora da lista |
| Tier sem seção no config | O `TierCatalog` usa o tier Base do robô e registra no log uma vez |
| Tipo de robô sem seção | Erro de programação, então o `TierCatalog` lança uma exceção |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-25 11:58 | Acrescenta as chaves do Network Server e do Solar Relay (perguntas 2334, 2349 e 2376). |
| Zennd | 2026-11-25 00:23 | Acrescenta as chaves do desenho da área, na seção `network` (perguntas 2287, 2311 e 2349). |
| Zennd | 2026-11-24 12:32 | Atualiza o contrato das chaves, que vale a partir da `1.0.0` (perguntas 2189 e 2190). |
| Zennd | 2026-11-24 09:56 | Acrescenta as chaves do XP do Dock (perguntas 1942 e 2103). |
| Zennd | 2026-11-24 08:37 | Acrescenta o dock_unreachable_ticks (perguntas 2091 e 2095). |
| Zennd | 2026-11-24 08:16 | Acrescenta as chaves do XP e tira as do XP como energia (perguntas 1817 a 1835). |
| Zennd | 2026-11-23 14:22 | Acrescenta a chave da fabricação em cascata (pergunta 2020). |
| Zennd | 2026-11-23 10:40 | Acrescenta a geração do painel do Dock à noite (pergunta 1944). |
| Zennd | 2026-11-23 01:58 | Cita a espera de 3 s do painel do Dock (pergunta 1912). |
| Zennd | 2026-11-23 00:09 | Acrescenta o tempo para o Dock montar e guardar o painel (pergunta 1856). |
| Zennd | 2026-11-22 23:45 | Passa a geração solar a todo Dock (pergunta 1855). |
| Zennd | 2026-11-22 22:00 | Acrescenta o tempo em que as asas continuam abertas (pergunta 1807). |
| Zennd | 2026-11-22 21:46 | Acrescenta a distância em que a Plasma Blade acende e o tempo em que ela continua acesa (perguntas 1804 e 1805). |
| Zennd | 2026-11-22 22:10 | Acrescenta a chave da parada da escolta (pergunta 1744). |
| Zennd | 2026-11-22 21:25 | Acrescenta a chave da descida planando (pergunta 1739). |
| Zennd | 2026-11-22 20:45 | Acrescenta as chaves da interceptação da bola de fogo (pergunta 1735). |
| Zennd | 2026-11-22 20:10 | Acrescenta as chaves do XP como energia do Patrulheiro (perguntas 1728 e 1729). |
| Zennd | 2026-11-22 18:10 | Acrescenta a chave do tempo até o alvo sumir de vista (pergunta 1721). |
| Zennd | 2026-11-22 14:50 | Acrescenta as chaves da cautela, do socorro e do retorno do Patrulheiro (perguntas 1686, 1689 e 1691). |
| Zennd | 2026-11-22 14:30 | Acrescenta as chaves que faltavam: o local de morte, a Engineering Desk, o Organizador, o Cozinheiro, a velocidade e as portas (pergunta 1684). |
| Zennd | 2026-11-22 14:05 | Acrescenta as chaves da apresentação do Cubit (perguntas 1674 e 1677). |
| Zennd | 2026-11-22 12:55 | Acrescenta as chaves do Patrulheiro e das falas das profissões (perguntas 1577, 1586, 1614 a 1651 e 1665). |
| Zennd | 2026-11-21 23:02 | Troca o Defensor pelo Patrulheiro Galático (perguntas 1550 a 1582). |
| Marcelo Fabiano | 2026-11-21 11:30 | Adiciona as chaves da Recharge Splash (perguntas 1413 e 1423). |
| Marcelo Fabiano | 2026-11-21 08:20 | Adiciona a geração do Dock Solar (pergunta 1385). |
| Marcelo Fabiano | 2026-11-21 06:30 | Leva o estoque do Dock para as seções de tier e adiciona a recarga e os slots (pergunta 1381). |
| Marcelo Fabiano | 2026-11-15 14:05 | Adiciona as chaves do intervalo de descarga (perguntas 1252 e 1254). |
| Marcelo Fabiano | 2026-11-15 13:25 | O `farmer_bone_meal` libera o slot de fertilizante (pergunta 1244). |
| Marcelo Fabiano | 2026-11-15 10:40 | Adiciona as chaves do Fazendeiro (perguntas 1226 e 1237). |
| Marcelo Fabiano | 2026-11-14 23:20 | Adiciona a chave `allowed_jobs`, da base do trabalho (pergunta 1205). |
| Marcelo Fabiano | 2026-11-14 23:10 | Adiciona as chaves do Pescador (perguntas 1184, 1185 e 1190). |
| Marcelo Fabiano | 2026-11-14 13:55 | Adiciona as chaves da descarga. |
| Marcelo Fabiano | 2026-11-14 10:00 | Adiciona a bateria do retorno automático. |
| Marcelo Fabiano | 2026-11-14 09:00 | Adiciona as chaves do modo área. |
| Marcelo Fabiano | 2026-11-13 17:15 | Adiciona o alcance da mira do app Alvo. |
| Marcelo Fabiano | 2026-11-13 15:10 | Troca o tempo do olhar pelo tempo depois do alvo. |
| Marcelo Fabiano | 2026-11-13 14:50 | Adiciona o tempo do olhar para o alvo do choque. |
| Marcelo Fabiano | 2026-11-13 13:45 | Adiciona as chaves das tochas. |
| Marcelo Fabiano | 2026-11-13 09:50 | Adiciona as chaves da busca dos itens do chão. |
| Marcelo Fabiano | 2026-11-12 22:30 | Adiciona a capacidade da Power Bank (pergunta 910). |
| Marcelo Fabiano | 2026-11-12 21:45 | Adiciona as chaves da coleta e do ímã (pergunta 889). |
| Marcelo Fabiano | 2026-11-12 15:10 | Adiciona o modo Focus (pergunta 850). |
| Marcelo Fabiano | 2026-11-12 10:30 | Adiciona as chaves do choque e dos modos de defesa (pergunta 834). |
| Marcelo Fabiano | 2026-11-11 22:01 | Adiciona as seções dos tiers Advanced, Elite e Ultimate. |
| Marcelo Fabiano | 2026-11-07 23:41 | Corrige os raios do Ultimate. |
| Marcelo Fabiano | 2026-11-07 20:04 | Adiciona o intervalo das falas da redstone. |
| Marcelo Fabiano | 2026-11-07 19:21 | Adiciona o tempo da caixa em volta de um minério. |
| Marcelo Fabiano | 2026-11-07 17:14 | Adiciona as chaves das falas de animais. |
| Marcelo Fabiano | 2026-11-07 17:11 | Adiciona a chave das falas de bioma. |
| Marcelo Fabiano | 2026-11-07 17:07 | Adiciona as chaves de ficar para trás. |
| Marcelo Fabiano | 2026-11-07 16:54 | Adiciona as chaves das falas. |
| Marcelo Fabiano | 2026-11-07 16:20 | Adiciona a altura máxima que a rota desce. |
| Marcelo Fabiano | 2026-11-07 15:57 | Adiciona as chaves dos scanners. |
| Marcelo Fabiano | 2026-11-07 13:51 | Aponta a margem das telas para a constante do jogo. |
| Marcelo Fabiano | 2026-11-06 15:05 | Adiciona as chaves do painel solar. |
| Marcelo Fabiano | 2026-11-06 13:34 | Adiciona as chaves da lanterna. |
| Marcelo Fabiano | 2026-11-06 11:10 | Tira a citação a um robô novo. |
| Marcelo Fabiano | 2026-11-06 11:10 | Troca 'especialidade' por 'trabalho' e 'card de especialidade' por 'card de Aprendizado de Máquina'. |
| Marcelo Fabiano | 2026-11-06 09:48 | Adiciona a chave do intervalo mínimo. |
| Marcelo Fabiano | 2026-11-06 00:28 | Adiciona a chave do tempo dos olhos em X. |
| Marcelo Fabiano | 2026-11-06 00:22 | Adiciona as chaves da expressão de preocupado. |
| Marcelo Fabiano | 2026-11-06 00:10 | Adiciona as chaves das expressões triste e quase morto. |
| Marcelo Fabiano | 2026-11-05 23:36 | Adiciona as chaves das expressões. |
| Marcelo Fabiano | 2026-11-05 21:30 | Registra a margem das telas como exceção. |
| Marcelo Fabiano | 2026-11-05 21:02 | Acompanha o nome ampliado do alcance em `tiers.md`. |
| Marcelo Fabiano | 2026-11-05 17:15 | Adiciona o tempo com a tela completa. |
| Marcelo Fabiano | 2026-11-05 16:09 | Adiciona as chaves do Induction Dock. |
| Marcelo Fabiano | 2026-11-05 14:40 | Adiciona as chaves do consumo por situação. |
| Marcelo Fabiano | 2026-11-05 12:52 | Adiciona as chaves da redstone. |
| Marcelo Fabiano | 2026-11-05 01:15 | Adiciona as chaves do conserto e do resgate no vazio. |
| Marcelo Fabiano | 2026-11-04 21:02 | Adiciona as chaves do comportamento em chamas. |
| Marcelo Fabiano | 2026-11-04 20:09 | Adiciona as chaves de movimento. |
| Marcelo Fabiano | 2026-11-04 19:24 | Adiciona a carga para religar. |
| Marcelo Fabiano | 2026-11-04 19:08 | Documenta as chaves, os limites e o tratamento de valor inválido. |
