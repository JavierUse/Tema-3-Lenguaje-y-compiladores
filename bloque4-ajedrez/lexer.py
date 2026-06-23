"""
Asignatura: Lenguajes y Compiladores (Sección 01).
Evaluación: Tema 3 - Bloque 4 
grupo: Team hexa
"""

import re
import sys

PGN_PATTERN = r"^[KQRBN]?[a-h]?x?[a-h][1-8]\+?$"

def evaluar_jugada_lexica(cadena_jugada: str) -> bool:

    cadena_limpia = cadena_jugada.strip()

    if re.match(PGN_PATTERN, cadena_limpia):
        print(f"TOKEN_VALOR: '{cadena_limpia:<8}' -> [STATUS: ACEPTADO] Estructura léxica válida.")
        return True
    else:
        print(f"TOKEN_VALOR: '{cadena_limpia:<8}' -> [STATUS: RECHAZADO] Error morfológico detectado.")
        return False

if __name__ == "__main__":
    casos_de_prueba = [
        "e4",       # Movimiento simple de peón
        "Nf3",      # Desplazamiento de caballo 
        "Bxf7+",    # Captura de alfil con provocación de jaque
        "exd5",     # Captura de peón con definición de columna origen
        "Qh5+",     # Desplazamiento de dama y jaque
        "Pe4",      # Inválido: La 'P' de peón no se escribe en la notación oficial PGN
        "x4",       # Inválido: Ausencia de la columna de destino en la captura
        "b9",       # Inválido: Fila fuera de la frontera ortogonal del tablero (1-8)
        "Nf3++",    # Inválido: Sintaxis desbordada por doble operador de jaque
        "e4x"       # Inválido: Malformación por carácter de acción desplazado al final
    ]
    
    print("=" * 68)
    print("        ANALIZADOR LÉXICO (LEXER) PARA NOTACIÓN AJEDREZ SUB-PGN")
    print("=" * 68)
    
    cadenas_aceptadas = 0
    for prueba in casos_de_prueba:
        if evaluar_jugada_lexica(prueba):
            cadenas_aceptadas += 1
            
    print("=" * 68)
    print(f"Resumen de Compilación Léxica: {cadenas_aceptadas}/{len(casos_de_prueba)} "
          f"cadenas evaluadas con éxito.")
    print("=" * 68)