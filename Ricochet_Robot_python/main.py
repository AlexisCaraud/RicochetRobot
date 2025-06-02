from plateau import *
from application import *
from resolution import *


if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = App()
    ex.showFullScreen()
    sys.exit(app.exec_())

