import sys
from PyQt5.QtWidgets import QApplication, QMainWindow, QGraphicsView, QGraphicsScene, QGraphicsRectItem
from PyQt5.QtCore import Qt, QRectF, QPointF
from PyQt5.QtGui import QColor, QBrush, QPen

# Exemple de robots et couleurs (à adapter)
ROBOT = ['rouge', 'vert', 'bleu', 'jaune']
COULEUR_ROBOT = {
    0: (255, 0, 0),
    1: (0, 255, 0),
    2: (0, 0, 255),
    3: (255, 255, 0)
}

class RobotItem(QGraphicsRectItem):
    def __init__(self, x, y, size, color, robot_id):
        self.size = size  # <-- définir size AVANT le super()
        super().__init__(0, 0, size, size)
        self.setBrush(QBrush(QColor(*color)))
        self.setPen(QPen(Qt.NoPen))
        self.setFlag(QGraphicsRectItem.ItemIsMovable, True)
        self.setFlag(QGraphicsRectItem.ItemSendsGeometryChanges, True)
        self.robot_id = robot_id
        self.setPos(x * size, y * size)

    def itemChange(self, change, value):
        if change == QGraphicsRectItem.ItemPositionChange:
            x = round(value.x() / self.size) * self.size
            y = round(value.y() / self.size) * self.size
            return QPointF(x, y)
        return super().itemChange(change, value)


class MapWindow(QMainWindow):
    def __init__(self, taille=10, precision=40):
        super().__init__()
        self.taille = taille
        self.precision = precision

        self.view = QGraphicsView()
        self.setCentralWidget(self.view)

        self.scene = QGraphicsScene()
        self.view.setScene(self.scene)

        self.init_map()
        self.init_robots()

        self.setWindowTitle("Carte interactive")
        self.resize(taille * precision + 20, taille * precision + 40)

    def init_map(self):
        # Dessine le quadrillage
        pen = QPen(QColor(200, 200, 200))
        for i in range(self.taille + 1):
            # lignes horizontales
            self.scene.addLine(0, i * self.precision, self.taille * self.precision, i * self.precision, pen)
            # lignes verticales
            self.scene.addLine(i * self.precision, 0, i * self.precision, self.taille * self.precision, pen)

    def init_robots(self):
        # Position initiale des robots (à adapter)
        positions = [(0,0), (3,4), (5,2), (7,7)]
        self.robot_items = []
        for i, (x,y) in enumerate(positions):
            robot = RobotItem(x, y, self.precision, COULEUR_ROBOT[i], i)
            self.scene.addItem(robot)
            self.robot_items.append(robot)


if __name__ == "__main__":
    app = QApplication(sys.argv)
    window = MapWindow()
    window.show()
    sys.exit(app.exec_())
