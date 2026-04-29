(https://trello.com/b/WzEoW4Cx/truco-club)  

-estados de juego:  
1 . ESTADO NORMAL : se pueden tirar cartas  
2 . ESTADO ENVIDO : las cartas estan bloqueadas hasta se responda el envido  

esquema del envido:  
1 . nadie canta nada; envidoCantado = false  
2 . Jugador A llama a envido; envidoCantado = true, puntosEnJuegoEnvido = 2  
3 . el sistema espera el input de Jugador B; pasa a ESTADO ENVIDO(ver seccion estados de juego)  
4 . Jugador B llama a responderEnvido, se procesa, se suman puntos; envidoCantado = false  

