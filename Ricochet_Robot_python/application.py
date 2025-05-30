import sys

import time

import os
os.system('color')

import colorama
colorama.init(convert=True, autoreset= True)

from PyQt5.QtWidgets import (QApplication, QMainWindow, QMenu, QProgressBar, QSpinBox, QGridLayout, 
QVBoxLayout, QSizePolicy, QMessageBox, QWidget, QPushButton, QRadioButton, QHBoxLayout, QLabel, QLineEdit, QGraphicsOpacityEffect)
from PyQt5.QtCore import Qt, QEvent, QPropertyAnimation
from PyQt5.QtGui import QIcon
from PyQt5.QtGui import QFont
from PyQt5 import QtCore
from PyQt5.QtCore import Qt
from PyQt5.QtCore import QTimer,QDateTime
from PyQt5.QtWidgets import QFileDialog  # <-- à ajouter en haut si pas encore importé
from PyQt5.QtWidgets import QComboBox  # Assure-toi que c'est importé
from PyQt5.QtWidgets import QInputDialog, QMessageBox
from PyQt5.QtWidgets import QDialog, QComboBox, QSpinBox, QDialogButtonBox

from PyQt5.QtWidgets import QMainWindow, QVBoxLayout, QWidget, QLabel
from matplotlib.backends.backend_qt5agg import FigureCanvasQTAgg as FigureCanvas
from matplotlib.figure import Figure


from functools import partial


from matplotlib.backends.backend_qt5agg import FigureCanvasQTAgg as FigureCanvas
from matplotlib.figure import Figure
import matplotlib.pyplot as plt
import subprocess

from random import randint
import numpy as np
from plateau import *
from resolution import *


class App(QMainWindow):

    def __init__(self):
        super().__init__()
        self.setWindowTitle("Ricochet Robot")
        self.setGeometry(100, 100, 1200, 800)

        self.size_case = 10  # taille d’une case en pixels dans l’image
        
        self.central_widget = QWidget()
        self.setCentralWidget(self.central_widget)
        self.layout = QHBoxLayout(self.central_widget)

        # Left side: Game Area
        self.canvas = PlotCanvas(self, width=6, height=6, ex=self)
        self.layout.addWidget(self.canvas)

        # Right side: Controls
        self.control_panel = QWidget()
        self.control_layout = QVBoxLayout()
        self.control_panel.setLayout(self.control_layout)
        self.layout.addWidget(self.control_panel)

        self.time_increment = 0
        self.time_record = 0
        self.init_controls()

    def init_controls(self):
        _translate = QtCore.QCoreApplication.translate

        self.coord_label = QLabel("Case : (-, -)")
        self.control_layout.addWidget(self.coord_label)


        # Title
        title_label = QLabel("Ricochet Robot Controls")
        title_label.setFont(QFont("Arial", 16, QFont.Bold))
        title_label.setAlignment(Qt.AlignCenter)
        self.control_layout.addWidget(title_label)

        # Text field
        self.position_input = self.create_input_field("Position Robot (e.g., r0, 0, 0)")
        self.map_name_input = self.create_input_field("Nom de la carte")

        self.nom_fichier = self.map_name_input.text()
        self.nom_modif = self.position_input.text()

        # Buttons Grid
        button_grid = QGridLayout()
        self.control_layout.addLayout(button_grid)

        self.new_map_btn = self.create_button("Nouvelle Map (c)", "c", self.canvas.begin)
        self.new_goal_btn = self.create_button("Nouvel Objectif (n)", "n", self.canvas.change)
        self.reset_btn = self.create_button("Reset (Space)", "Space", self.canvas.reset)
        self.validate_btn = self.create_button("Valider (v)", "v", self.canvas.register)
        self.load_btn = self.create_button("Ouvrir Map (o)", "o", self.canvas.ouvrir)
        self.exit_btn = self.create_button("Terminer (t)", "t", self.exit_game)
        self.previous_btn = self.create_button("Précédent (p)", "p", self.canvas.previous)
        # self.modifier_carte_btn = self.create_button("Modifer Carte (m)", "m", self.canvas.modif)
        # self.modifier_carte_btn = self.create_button("Modifier Carte (m)", "m", self.prompt_modify_robot_position)
        self.modifier_carte_btn = self.create_button("Modifier Carte (m)", "m", self.prompt_modify_element_position)
        # self.save_btn = self.create_button("Sauvegarder (s)", "s", self.canvas.save)
        # self.save_btn = self.create_button("Sauvegarder (s)", "s", lambda: self.canvas.save(self.nom_fichier))
        # self.save_btn = self.create_button("Sauvegarder (s)", "s",lambda: (self.canvas.save(self.nom_fichier), self.refresh_map_list()))
        self.save_btn = self.create_button("Sauvegarder (s)", "s", self.prompt_save_map)

        self.map_real_btn = self.create_button("Ouvrir Vraie Map (w)", "w", self.canvas.ouvrir_vrai_map)

        button_grid.addWidget(self.new_map_btn, 0, 0)
        button_grid.addWidget(self.new_goal_btn, 0, 1)
        button_grid.addWidget(self.reset_btn, 1, 0)
        button_grid.addWidget(self.validate_btn, 1, 1)
        button_grid.addWidget(self.load_btn, 2, 0)
        button_grid.addWidget(self.exit_btn, 2, 1)
        button_grid.addWidget(self.previous_btn, 3, 0)
        button_grid.addWidget(self.modifier_carte_btn, 3, 1)
        button_grid.addWidget(self.save_btn, 4, 0)
        button_grid.addWidget(self.map_real_btn, 4, 1)

        # Solution Buttons
        self.solution_layout = QVBoxLayout()
        self.control_layout.addLayout(self.solution_layout)

        self.fast_btn = self.create_button("Rapide", None, self.canvas.sol_fast)
        self.non_optim_btn = self.create_button("BFS", None, self.canvas.sol_bfs)
        self.optim_btn = self.create_button("AStar", None, self.canvas.sol_aStar)

        self.solution_layout.addWidget(self.fast_btn)
        self.solution_layout.addWidget(self.non_optim_btn)
        self.solution_layout.addWidget(self.optim_btn)

        self.solution_labels_layout = QVBoxLayout()

        self.label_sol_fast = QLabel('Solution:', self)
        self.label_sol_fast.setAlignment(Qt.AlignLeft)
        self.label_sol_fast.setVisible(False)

        self.label_sol_non_optim = QLabel('Solution:', self)
        self.label_sol_non_optim.setAlignment(Qt.AlignLeft)
        self.label_sol_non_optim.setVisible(False)

        self.label_optim = QLabel('Solution: ', self)
        self.label_optim.setAlignment(Qt.AlignLeft)
        self.label_optim.setVisible(False)

        # Ajout des labels dans le layout de solutions
        self.solution_labels_layout.addWidget(self.label_sol_fast)
        self.solution_labels_layout.addWidget(self.label_sol_non_optim)
        self.solution_labels_layout.addWidget(self.label_optim)

        # Ajoute le layout au layout principal
        self.control_layout.addLayout(self.solution_labels_layout)


        # Robot Selection
        '''robot_label = QLabel("\u2699 Robots")
        robot_label.setFont(QFont("Arial", 14))
        robot_label.setAlignment(Qt.AlignCenter)
        self.control_layout.addWidget(robot_label)'''

        self.robot_buttons = []
        for idx, robot in enumerate(ROBOT):
            robot_button = QRadioButton(f"Robot {robot} {idx}")
            robot_button.setStyleSheet(f"color: {robot}")
            robot_button.setShortcut(_translate("MainWindow", f'{idx}'))
            if idx == 0:
                robot_button.setChecked(True)
            robot_button.clicked.connect(lambda _, r=robot: self.canvas.appui_bouton(r))
            self.robot_buttons.append(robot_button)
            self.control_layout.addWidget(robot_button)

        # Position and Map Name
        # self.position_input = self.create_input_field("Position Robot (e.g., r0, 0, 0)")
        # self.map_name_input = self.create_input_field("Nom de la carte")
        self.control_layout.addWidget(self.position_input)
        self.control_layout.addWidget(self.map_name_input)

        # self.nom_fichier = self.map_name_input.text()
        # self.nom_modif = self.position_input.text()

        self.map_name_input.textChanged.connect(lambda: self.canvas.changer_nom_map())
        self.position_input.textChanged.connect(lambda: self.canvas.changer_position())

        # ComboBox pour sélectionner une carte
        self.map_selector = QComboBox()
        self.map_selector.setFont(QFont("Arial", 12))
        self.map_selector.setStyleSheet("padding: 5px; border: 1px solid gray; border-radius: 5px;")
        self.refresh_map_list()  # Initialise la liste
        self.map_selector.currentIndexChanged.connect(self.update_map_selection)

        self.control_layout.addWidget(self.map_selector)

        # Info Labels
        self.record_label = QLabel("Record: {}".format(self.time_increment))
        self.counter_label = QLabel("Compteur: {}".format(self.time_record))
        self.control_layout.addWidget(self.record_label)
        self.control_layout.addWidget(self.counter_label)

        self.haut = QPushButton('haut', self)
        self.haut.move(0,300)
        self.haut.setGeometry(0,0,0,0)
        self.haut.setShortcut(_translate("MainWindow", "Up"))

        self.bas = QPushButton('bas', self)
        self.bas.move(0,350)
        self.bas.setGeometry(0,0,0,0)
        self.bas.setShortcut(_translate("MainWindow", "Down"))

        self.droite = QPushButton('droite', self)
        self.droite.move(0,400)
        self.droite.setGeometry(0,0,0,0)
        self.droite.setShortcut(_translate("MainWindow", "Right"))

        self.gauche = QPushButton('gauche', self)
        self.gauche.move(0,450)
        self.gauche.setGeometry(0,0,0,0)
        self.gauche.setShortcut(_translate("MainWindow", "Left"))

        for rang, robot in enumerate(ROBOT):
            self.robot_buttons[rang].clicked.connect(partial(self.canvas.appui_bouton, robot))


        self.gauche.clicked.connect(lambda: self.canvas.depp('gauche'))
        self.droite.clicked.connect(lambda: self.canvas.depp('droite'))
        self.haut.clicked.connect(lambda: self.canvas.depp('haut'))
        self.bas.clicked.connect(lambda: self.canvas.depp('bas'))

        self.control_layout.addStretch()

    def create_button(self, text, shortcut, callback):
        button = QPushButton(text)
        button.setFont(QFont("Arial", 12))
        button.setStyleSheet("background-color: #4CAF50; color: white; padding: 10px;")
        button.clicked.connect(callback)
        if shortcut:
            button.setShortcut(shortcut)
        return button
    
    def create_input_field(self, placeholder):
        input_field = QLineEdit()
        input_field.setPlaceholderText(placeholder)
        input_field.setFont(QFont("Arial", 12))
        input_field.setStyleSheet("padding: 5px; border: 1px solid gray; border-radius: 5px;")
        return input_field
    
    def refresh_map_list(self):
        self.map_selector.clear()
        map_dir = "../Map"
        if os.path.exists(map_dir):
            maps = [f[:-4] for f in os.listdir(map_dir) if f.endswith(".txt")]
            self.map_selector.addItems(sorted(maps))

    def update_map_selection(self):
        self.nom_fichier = self.map_selector.currentText()

    def prompt_save_map(self):
        text, ok = QInputDialog.getText(self, 'Sauvegarder la carte', 'Nom du fichier:')
        if ok and text.strip():
            nom_fichier = text.strip()
            chemin_fichier = f"map/{nom_fichier}.txt"

            # Vérifie si le fichier existe déjà
            if os.path.exists(chemin_fichier):
                confirm = QMessageBox.question(
                    self,
                    "Fichier existant",
                    f"Le fichier '{nom_fichier}.txt' existe déjà.\nVoulez-vous l'écraser ?",
                    QMessageBox.Yes | QMessageBox.No,
                    QMessageBox.No
                )
                if confirm != QMessageBox.Yes:
                    return  # Ne sauvegarde pas si l'utilisateur a dit non

            # Si tout est bon : sauvegarde
            self.nom_fichier = nom_fichier
            self.canvas.save(self.nom_fichier)
            self.refresh_map_list()

    def prompt_modify_element_position(self):
        dialog = QDialog(self)
        dialog.setWindowTitle("Modifier une position")
        layout = QVBoxLayout(dialog)

        # Choix du type à modifier
        type_selector = QComboBox(dialog)
        type_selector.addItem("Robot", "robot")
        type_selector.addItem("Objectif", "objectif")
        layout.addWidget(QLabel("Choisissez le type à modifier :"))
        layout.addWidget(type_selector)

        # Choix de l'élément (r0...r3 ou o0...o3)
        element_selector = QComboBox(dialog)
        for i, color in enumerate(ROBOT):
            element_selector.addItem(f"Robot {i} ({color})", f"r{i}")
        layout.addWidget(QLabel("Choisissez l'élément :"))
        layout.addWidget(element_selector)

        # Met à jour le menu déroulant selon le type
        def update_element_list():
            element_selector.clear()
            if type_selector.currentData() == "robot":
                for i, color in enumerate(ROBOT):
                    element_selector.addItem(f"Robot {i} ({color})", f"r{i}")
            else:
                for i, color in enumerate(ROBOT):
                    element_selector.addItem(f"Objectif {i} ({color})", f"o{i}")
        type_selector.currentIndexChanged.connect(update_element_list)

        # Coordonnées
        x_spin = QSpinBox(dialog)
        y_spin = QSpinBox(dialog)
        x_spin.setRange(0, N - 1)
        y_spin.setRange(0, N - 1)

        layout.addWidget(QLabel("Coordonnée X :"))
        layout.addWidget(x_spin)
        layout.addWidget(QLabel("Coordonnée Y :"))
        layout.addWidget(y_spin)

        # Boutons
        buttons = QDialogButtonBox(QDialogButtonBox.Ok | QDialogButtonBox.Cancel, dialog)
        buttons.accepted.connect(dialog.accept)
        buttons.rejected.connect(dialog.reject)
        layout.addWidget(buttons)

        if dialog.exec() == QDialog.Accepted:
            code = element_selector.currentData()  # ex : r0 ou o2
            x = x_spin.value()
            y = y_spin.value()
            self.nom_modif = f"{code},{x},{y}"
            self.canvas.modif()

    
    def exit_game(self):
        sys.exit(0)


class PlotCanvas(FigureCanvas):

    def __init__(self, parent=None, width=10, height=10, dpi=100, ex=None):
        self.app = ex
        self.jeu = Plateau(N, PRECISION)
        self.jeu.random()
        self.historique = [self.jeu.get_coord_robots()]
        self.objectif = self.jeu.objectif.couleur
        self.couleur = ROBOT[0] # robot 0 par défaut
        self.algo = Algo(self.jeu)
        fig = Figure(figsize=(width, height), dpi=dpi)
        self.axes = fig.add_subplot(111)



        FigureCanvas.__init__(self, fig)
        self.setParent(parent)

        FigureCanvas.setSizePolicy(self,
                QSizePolicy.Expanding,
                QSizePolicy.Expanding)
        FigureCanvas.updateGeometry(self)
        self.plot()

        self.mpl_connect("motion_notify_event", self.mouse_move_event)


    # def plot(self):
    #     a = self.jeu.affich()
    #     self.objectif = self.jeu.objectif.couleur
    #     self.figure.clf()  # Clear the figure
    #     ax = self.figure.add_subplot(111)
    #     ax.imshow(a, interpolation='nearest')
    #     ax.set_title('Ricochet Robot')
    #     ax.axis('off')
    #     self.draw()

    def mouse_move_event(self, event):
        if event.inaxes:  # Vérifie si la souris est dans les axes du graphe
            col = int(event.xdata) // self.jeu.précision
            row = int(event.ydata) // self.jeu.précision
            self.app.coord_label.setText(f"Case : ({row}, {col})")
        else:
            self.app.coord_label.setText("Case : (-, -)")


    def plot(self):
        a = self.jeu.affich()
        self.objectif = self.jeu.objectif.couleur
        self.figure.clf()  # Clear the figure
        ax = self.figure.add_subplot(111)
        ax.imshow(a, interpolation='nearest')
        ax.set_title('Ricochet Robot')
        ax.axis('off')
        
        self.draw()



    def begin(self):
        self.app.time_record,self.app.time_increment = 0,0
        self.app.record_label.setText("Record: " + str(self.app.time_record))
        self.app.counter_label.setText("Compteur: " + str(self.app.time_increment))
        self.app.label_sol_fast.setVisible(False)
        self.app.label_sol_non_optim.setVisible(False)
        self.app.label_optim.setVisible(False)
        self.jeu.random()
        self.save("mapActuelle")
        self.historique = [self.jeu.get_coord_robots()]
        self.plot()

    def change(self):
        self.app.time_record,self.app.time_increment = 0,0
        self.app.record_label.setText("Record: " + str(self.app.time_record))
        self.app.counter_label.setText("Compteur: " + str(self.app.time_increment))
        self.jeu.rand_obj()
        self.save("mapActuelle")
        self.plot()

    def reset(self):
        self.app.time_increment = 0
        self.app.counter_label.setText("Compteur: " + str(self.app.time_increment))
        self.jeu.replacer_robots(self.jeu.init)
        self.historique = [self.jeu.get_coord_robots()]
        self.app.label_sol_fast.setVisible(False)
        self.app.label_sol_non_optim.setVisible(False)
        self.app.label_optim.setVisible(False)
        self.plot()

    def register(self):
        if (self.app.time_record==0 or self.app.time_record > self.app.time_increment) and self.jeu.est_resolu():
            self.app.time_record = self.app.time_increment
            self.app.record_label.setText("Record: " + str(self.app.time_record))
            self.app.time_increment = 0
            self.app.counter_label.setText("Compteur: " + str(self.app.time_increment))
            self.message = QMessageBox(self)
            self.message.setText('Gagné !')
            self.message.exec()

    def sol_fast(self):
        # sol = self.algo.solution_fast(self.objectif, np.inf)
        # self.app.label_sol_fast.setText("Solution Rapide :" + self.affiche_texte_couleur(sol, self.objectif))
        # self.app.label_sol_fast.setVisible(True)
        # self.jeu.replacer_robots(self.jeu.init)
        # self.historique = [self.jeu.get_coord_robots()]

        # print(self.jeu.get_coord_robots())
        # print(self.jeu.objectif.coord())
        # print(self.jeu.objectif.couleur)
        # print(self.jeu.list_coins)

        self.save("mapActuelle")
        start = time.perf_counter()
        classpath = os.path.abspath('../Ricochet_Robot_Java/bin')  # Dossier "bin" relatif au script Python
        commande = ['java', '-cp', classpath, 'ricochetRobot.Launcher.FastLauncher']  # Le nom de la classe sans l'extension .class
        resultat = subprocess.run(commande, capture_output=True, text=True)
        end =  time.perf_counter()
        sol_aStar = self.convert_solution_to_list(resultat.stdout.strip())
        sol_aStar[2] = end - start
        self.app.label_sol_fast.setText("Solution Rapide :" + self.affiche_texte_couleur(sol_aStar, self.objectif))
        self.app.label_sol_fast.setVisible(True)
        self.jeu.replacer_robots(self.jeu.init)
        self.historique = [self.jeu.get_coord_robots()]


    
    def sol_aStar(self):
        # start = time.perf_counter()
        # sol = self.algo.solution_fast(self.objectif, np.inf)
        # sol_aStar = self.algo.branch_and_bound(self.objectif, sol, self.algo.nombre_dep_sec(sol), True)
        # end =  time.perf_counter()
        # sol_aStar [2] = end-start
        # self.app.label_optim.setText("Solution Optimale :" + self.affiche_texte_couleur(sol_aStar, self.objectif))

        self.save("mapActuelle")
        start = time.perf_counter()
        classpath = os.path.abspath('../Ricochet_Robot_Java/bin')  # Dossier "bin" relatif au script Python
        commande = ['java', '-cp', classpath, 'ricochetRobot.Launcher.BFSLauncher2']  # Le nom de la classe sans l'extension .class
        resultat = subprocess.run(commande, capture_output=True, text=True)
        end =  time.perf_counter()
        sol_aStar = self.convert_solution_to_list(resultat.stdout.strip())
        sol_aStar[2] = end - start
        self.app.label_optim.setText("Solution Optimale AStar :" + self.affiche_texte_couleur(sol_aStar, self.objectif))
        self.app.label_optim.setVisible(True)
        self.jeu.replacer_robots(self.jeu.init)
        self.historique = [self.jeu.get_coord_robots()]

    def sol_bfs(self):
        # start = time.perf_counter()
        # sol = self.algo.solution_fast(self.objectif, np.inf)
        # sol_aStar = self.algo.branch_and_bound(self.objectif, sol, self.algo.nombre_dep_sec(sol))
        # end =  time.perf_counter()
        # sol_aStar [2] = end-start
        # self.app.label_sol_non_optim.setText("Solution non Optimale :" + self.affiche_texte_couleur(sol_aStar, self.objectif))
        # self.app.label_sol_non_optim.setVisible(True)
        # self.jeu.replacer_robots(self.jeu.init)
        # self.historique = [self.jeu.get_coord_robots()]

        self.save("mapActuelle")
        start = time.perf_counter()
        classpath = os.path.abspath('../Ricochet_Robot_Java/bin')  # Dossier "bin" relatif au script Python
        commande = ['java', '-cp', classpath, 'ricochetRobot.Launcher.BFSLauncher']  # Le nom de la classe sans l'extension .class
        resultat = subprocess.run(commande, capture_output=True, text=True)
        end =  time.perf_counter()
        sol_aStar = self.convert_solution_to_list(resultat.stdout.strip())
        sol_aStar[2] = end - start
        self.app.label_sol_non_optim.setText("Solution BFS :" + self.affiche_texte_couleur(sol_aStar, self.objectif))
        self.app.label_sol_non_optim.setVisible(True)
        self.jeu.replacer_robots(self.jeu.init)
        self.historique = [self.jeu.get_coord_robots()]

    def convert_solution_to_list(self, input_str):
        # Séparer la chaîne en deux parties : "nbr_coups" et "deplacements"
        parts = input_str.split(' deplacements: ')

        # Extraire le nombre de coups
        nbr_coups_part = parts[0].strip()

        nbr_coups = int(nbr_coups_part.split(':')[2].strip())

        # Extraire les déplacements (avant 'time:')
        deplacements_part = parts[1].split(' time: ')[0].strip()
        deplacements = deplacements_part.split(' ')

        # Créer la liste finale
        result = [nbr_coups, deplacements, 0]
        
        return result

    def affiche_texte_couleur(self, texte, objectif):
        chaine = str(texte[0]) + " coups, "
        flèches = {"HAUT": "🠕", "DROITE": "🠖", "BAS": "🠗", "GAUCHE": "🠔", 
               "haut": "🠕", "droite": "🠖", "bas": "🠗", "gauche": "🠔"}
        for i in range(0, len(texte[1]) - 1, 2):
            if texte[1][i] in ROBOT:
                chaine += f'<span style="color:{texte[1][i]}; font-weight: bold; font-size: 48px;">{flèches[texte[1][i+1]]}</span> '
            else:
                chaine += f'<span style="color:{ROBOT[int(texte[1][i])]}; font-weight: bold; font-size: 48px;">{flèches[texte[1][i+1]]}</span> '
        chaine += ", " + str(round(texte[2], 1)) + " secondes"
        return chaine

    # def affiche_texte_couleur(self, texte, objectif):
    #  chaine = '['
    #  chaine += str(texte[0]) + ", "
    #  flèches = {"HAUT": "↑", "DROITE": "→", "BAS": "↓", "GAUCHE": "←"}
     
    #  for i in range(0, len(texte[1]) - 1, 2):
    #      direction = texte[1][i]
    #      if direction in flèches:
    #          flèche = flèches[direction]  # Remplace le mot par une flèche
    #      else:
    #          flèche = direction  # Si c'est un autre cas, garde le texte tel quel
         
    #      # Utiliser le code couleur pour afficher la flèche dans la couleur correspondante
    #      if direction in ROBOT:
    #          chaine += f'<span style="color:{direction};">{flèche}</span> '
    #      else:
    #          chaine += f'<span style="color:{ROBOT[int(direction)]};">{flèche}</span> '
    #  chaine += ']'
    #  print(chaine)
    #  return chaine

    
    def sameColor(self, col, robot):
        
        if col == 'BLEU':
            return robot == 'cyan'
        elif col == 'VERT':
            return robot == 'chartreuse'
        elif col == 'VIOLET':
            return robot == 'darkviolet'
        elif col == 'ROUGE':
            return robot == 'red'
        else:
            return col == robot
    
    def convertir_liste_coup(self,list_coup, main): # convertit en mettant aussi la couleur du robot principal
        new_list_coup = []
        if list_coup[0] in DIR:
            for l in range(len(list_coup)):
                new_list_coup.append(main)
                new_list_coup.append(list_coup[l])
            return new_list_coup
        for l in range(len(list_coup)):
            if list_coup[l] in ROBOT:
                new_list_coup.append(list_coup[l])
                new_list_coup.append(list_coup[l+1])
            elif l>0 and list_coup[l-1] not in ROBOT:
                new_list_coup.append(main)
                new_list_coup.append(list_coup[l])
        return new_list_coup






    def previous(self):
        if self.app.time_increment >=1:
            self.app.time_increment -= 1
            self.app.counter_label.setText("Compteur: " + str(self.app.time_increment))
            self.jeu.replacer_robots(self.historique[-2])
            self.historique =copy.deepcopy(self.historique[:-1])
            self.plot()


    def appui_bouton(self, couleur):
        self.couleur = couleur

    def depp(self, direction):
        self.app.time_increment += 1
        self.app.counter_label.setText("Compteur: " + str(self.app.time_increment))
        self.jeu.dep(self.couleur, direction)
        self.historique.append(self.jeu.get_coord_robots())
        self.plot()

    def changer_nom_map(self):
         self.app.nom_fichier = self.app.map_name_input.text()

    def changer_position(self):
         self.app.nom_modif = self.app.position_input.text()

    '''def ouvrir_vrai_map(self):
        Q1 = Quadrant("soleil", "recto")
        Q2 = Quadrant("étoile", "verso")
        Q2.rotation_90_degre_droite()
        Q3 = Quadrant("planète", "recto")
        Q3.rotation_90_degre_gauche()
        Q4 = Quadrant("lune", "verso")
        Q4.rotation_90_degre_gauche().rotation_90_degre_gauche()
        self.jeu.generer_vrai_plateau(Q1, Q2, Q3, Q4)
        self.app.time_increment = 0
        self.app.counter_label.setText("Compteur: " + str(self.app.time_increment))
        self.save("mapActuelle")
        self.historique = [self.jeu.get_coord_robots()]
        self.plot()'''
    
    def ouvrir_vrai_map(self):
        dialog = QuadrantSelectionDialog()
        if dialog.exec_() == QDialog.Accepted:
            selections = dialog.getSelections()  # liste de 4 tuples (type, orientation)

            quadrants = []
            for type_nom, face in selections:
                q = Quadrant(type_nom, face)
                # Orientation selon le quadrant : 
                idx = len(quadrants)
                if idx == 1:
                    q.rotation_90_degre_droite()
                elif idx == 2:
                    q.rotation_90_degre_gauche()
                elif idx == 3:
                    q.rotation_90_degre_gauche().rotation_90_degre_gauche()
                quadrants.append(q)

            Q1, Q2, Q3, Q4 = quadrants
            self.jeu.generer_vrai_plateau(Q1, Q2, Q3, Q4)

            self.app.time_increment = 0
            self.app.counter_label.setText("Compteur: " + str(self.app.time_increment))
            self.save("mapActuelle")
            self.historique = [self.jeu.get_coord_robots()]
            self.plot()



    def save(self, nom_fichier):
        lines = ""
        path = '../Map/%s.txt' % (nom_fichier)
        with open (path, 'w') as f:
            for i in range (N):
                for j in range (N):
                    case = self.jeu.tableau[i][j]
                    for rang, robot in enumerate(ROBOT):
                        if case.robot.couleur == robot:
                            lines += "r%d,%d,%d\n" % (rang,i,j)
                        if case.objectif.couleur == robot:
                            lines += "o%d,%d,%d\n" % (rang,i,j)

                    if (i == 0 or i == N-1) and j != N-1 and case.mur.droite and self.jeu.tableau[i][j+1].mur.gauche:
                        lines += "bv,%d,%d\n" % (i,j)
                    if (j == 0 or j == N-1) and i != N-1 and case.mur.bas and self.jeu.tableau[i+1][j].mur.haut:
                        lines += "bh,%d,%d\n" % (i,j)
                    if i !=0 and i != N-1 and j !=0 and j != N-1:
                        if case.mur.haut and case.mur.droite:
                            lines += "c_h_d,%d,%d\n" % (i,j)
                        if case.mur.bas and case.mur.droite:
                            lines += "c_b_d,%d,%d\n" % (i,j)
                        if case.mur.bas and case.mur.gauche:
                            lines += "c_b_g,%d,%d\n" % (i,j)
                        if case.mur.gauche and case.mur.haut:
                            lines += "c_h_g,%d,%d\n" % (i,j)
            f.write(lines)

    def modif(self):
        change = self.app.nom_modif.split(',')
        x, y = int(change[1]), int(change[2])
        dic_robot = {}
        dic_obj = {}
        for rang, robot in enumerate(ROBOT):
            dic_robot['r%d' % (rang)] = robot   #dic_robot = {'r0': 'red', 'r1':etc...}
            dic_obj['o%d' % (rang)] = robot     #dic_obj = {'o0': 'red', 'o1':etc...}
        for cle in dic_robot.keys():
            if cle == change[0]:
                for i in range (N):
                    for j in range (N):
                        case = self.jeu.tableau[i][j]
                        if case.robot.couleur == dic_robot[cle]:
                            self.jeu.bouger_robot(case.robot.couleur, x, y)
        self.jeu.coord_init()
        for cle in dic_obj.keys():
            if change[0] in(cle):
                for i in range (N):
                        for j in range (N):
                            case = self.jeu.tableau[i][j]
                            if case.objectif.couleur != None:
                                self.jeu.bouger_obj(dic_obj[cle],x, y)
        self.app.time_increment = 0
        self.app.counter_label.setText("Compteur: " + str(self.app.time_increment))
        self.historique = [self.jeu.get_coord_robots()]
        self.save("mapActuelle")
        self.plot()


    # def ouvrir(self):
    #     self.jeu.open(self.app.nom_fichier)
    #     self.app.time_increment = 0
    #     self.app.counter_label.setText("Compteur: " + str(self.app.time_increment))
    #     self.historique = [self.jeu.get_coord_robots()]
    #     self.plot()

    def ouvrir(self):
        self.jeu.open(self.app.nom_fichier)
        self.app.time_increment = 0
        self.app.counter_label.setText("Compteur: " + str(self.app.time_increment))
        self.historique = [self.jeu.get_coord_robots()]
        self.plot()


    # def ouvrir(self):
    #     options = QFileDialog.Options()
    #     options |= QFileDialog.ReadOnly
    #     file_path, _ = QFileDialog.getOpenFileName(
    #         self,
    #         "Ouvrir une carte",
    #         "map/",  # répertoire par défaut
    #         "Fichiers texte (*.txt);;Tous les fichiers (*)",
    #         options=options
    #     )
    #     if file_path:
    #         # Extraire uniquement le nom du fichier sans l’extension
    #         file_name = os.path.basename(file_path)
    #         file_name = os.path.splitext(file_name)[0]

    #         self.app.nom_fichier = file_name
    #         self.jeu.open(file_name)
    #         self.app.time_increment = 0
    #         self.app.counter_label.setText("Compteur: " + str(self.app.time_increment))
    #         self.historique = [self.jeu.get_coord_robots()]
    #         self.plot()

    def end(self):
        sys.exit(0)

from PyQt5.QtWidgets import QDialog, QVBoxLayout, QHBoxLayout, QLabel, QComboBox, QPushButton

from PyQt5.QtWidgets import QDialog, QVBoxLayout, QHBoxLayout, QLabel, QComboBox, QPushButton

class QuadrantSelectionDialog(QDialog):
    def __init__(self, parent=None):
        super().__init__(parent)
        self.setWindowTitle("Sélectionner les quadrants")
        layout = QVBoxLayout()

        # Noms lisibles pour chaque position de quadrant
        labels = [
            "Quadrant haut gauche",
            "Quadrant haut droite",
            "Quadrant bas gauche",
            "Quadrant bas droite"
        ]

        self.combos = []
        for i in range(4):
            row = QHBoxLayout()
            label = QLabel(labels[i])
            row.addWidget(label)

            combo = QComboBox()
            combo.addItems(["soleil", "étoile", "planète", "lune"])
            row.addWidget(combo)

            orientation = QComboBox()
            orientation.addItems(["recto", "verso"])
            row.addWidget(orientation)

            layout.addLayout(row)
            self.combos.append((combo, orientation))

        # Boutons OK / Annuler
        button_layout = QHBoxLayout()
        ok_button = QPushButton("OK")
        cancel_button = QPushButton("Annuler")
        button_layout.addWidget(ok_button)
        button_layout.addWidget(cancel_button)

        ok_button.clicked.connect(self.accept)
        cancel_button.clicked.connect(self.reject)

        layout.addLayout(button_layout)
        self.setLayout(layout)

    def getSelections(self):
        # Retourne les paires (nom_quadrant, orientation)
        return [(combo.currentText(), orient.currentText()) for combo, orient in self.combos]



if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = App()
    ex.showFullScreen()
    sys.exit(app.exec_())