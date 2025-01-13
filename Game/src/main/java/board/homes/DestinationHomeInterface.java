package board.homes;

import board.Field;
import board.enums.PieceColor;
import java.util.List;
import java.util.Map;

public interface DestinationHomeInterface {
    Map<PieceColor, List<Field>> getDestinationHomesMap(); // Metoda zwracająca mapę domków docelowych
}
