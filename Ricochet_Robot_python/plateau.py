from matplotlib import colors
import numpy as np
import time
from random import *
import webcolors
import colorsys
import matplotlib.pyplot as plt
from matplotlib.colors import to_rgb

from scipy.spatial import KDTree
from webcolors import (CSS3_HEX_TO_NAMES , hex_to_rgb)

def generate_distinct_colors(n):
    colors = []
    for i in range(n):
        hue = float(i) / n
        saturation = 1.0
        value = 1.0
        rgb = colorsys.hsv_to_rgb(hue, saturation, value)
        colors.append(tuple(int(c * 255) for c in rgb))
    return colors

def generate_distinct_colors_matplotlib(n, cmap_name='tab10'):
    """
    Génère n couleurs RGB visuellement distinctes à l'aide d'une palette matplotlib.
    
    :param n: nombre de couleurs à générer
    :param cmap_name: nom de la colormap à utiliser ('tab10', 'tab20', 'Set3', etc.)
    :return: liste de tuples RGB (valeurs 0–255)
    """
    cmap = plt.get_cmap(cmap_name)
    colors = []
    for i in range(n):
        color = cmap(i % cmap.N)  # boucle si n > cmap.N
        rgb = tuple(int(c * 255) for c in color[:3])  # on ignore l'alpha
        colors.append(rgb)
    return colors

# Couleurs classiques de Ricochet Robot (ordre : jaune, vert, rouge, bleu)
BASE_RICOCHET_COLORS = [
    "#FFFF00", "#00AA00", "#CC0000", "#0000FF",
    "#8B1B84", "#72B8D6", "#C56710", "#814D08FD",
    "#882255", "#661100", "#6699CC", "#888888"
]

# Couleurs qualitatives Tol (jusqu'à 12 couleurs)
TOL_HEX = [
    "#332288", "#117733", "#88CCEE", "#DDCC77",
    "#CC6677", "#AA4499", "#44AA99", "#999933",
    "#882255", "#661100", "#6699CC", "#888888"
]

def generate_tol_colors(n):
    """
    Génère n couleurs distinctes :
    - Si n == 4 : utilise les couleurs classiques de Ricochet Robot
    - Si n <= 12 : utilise la palette de Paul Tol
    - Si n > 12 : utilise une génération HSV comme fallback
    
    :param n: nombre de couleurs demandées
    :return: liste de tuples RGB (valeurs 0–255)
    """
    if n <= len(BASE_RICOCHET_COLORS):
        return [tuple(int(c * 255) for c in to_rgb(hex)) for hex in BASE_RICOCHET_COLORS[:n]]
    else:
        return generate_distinct_colors(n)

def convert_rgb_to_names(rgb_tuple):
    
    # a dictionary of all the hex and their respective names in css3
    css3_db = CSS3_HEX_TO_NAMES 
    names = []
    rgb_values = []    
    for color_hex, color_name in css3_db.items():
        names.append(color_name)
        rgb_values.append(hex_to_rgb(color_hex))
    
    kdt_db = KDTree(rgb_values)    
    distance, index = kdt_db.query(rgb_tuple)
    return names[index]

N = 16 # taille de la grille
CENTRAL = 2 # taille de la case centrale
PRECISION = 10   # précision d'affichage
NBR_ROBOT = 4  # nombre de robot
NBR_COIN = 1 # nombre de coins différents par quart de plateau
NBR_BARRE = 1 # nombre de barres verticales (et horizontales) par quart de plateau
COULEUR_ROBOT_RVB = generate_distinct_colors(NBR_ROBOT)
# COULEUR_ROBOT_RVB = [
#     (255, 0, 0),
#     (0, 255, 0),
#     (0, 0, 255),
#     (128, 0, 128)
# ]
ROBOT = [convert_rgb_to_names(tuple) for tuple in COULEUR_ROBOT_RVB]
# ROBOT = ["rouge", "vert", "bleu", "violet"]
DIR=['HAUT', 'DROITE', 'BAS', 'GAUCHE']     # liste des déplacements


class Robot:
	def __init__(self, couleur=None, x=None, y=None, principal=False):
		self.couleur = couleur
		self.x = x
		self.y = y
		self.principal = principal	#True si c'est le robot principal

	def coord(self):
		return (self.x, self.y)

	def __repr__(self):
		# return 'x:{} y:{} couleur:{}'.format(self.x, self.y, self.couleur)
		return 'robot:{}'.format(self.couleur)
	

class Objectif:
	def __init__(self,  x=None, y=None, couleur=None):
		self.couleur = couleur
		self.x = x
		self.y = y

	def coord(self):
		return (self.x, self.y)

	def __repr__(self):
		# return 'x:{} y:{} couleur:{}'.format(self.x, self.y, self.couleur)
		return 'objectif:{}'.format(self.couleur)

class Mur:
	def __init__(self, *args): # args: directions où le mur est présent
		self.haut = False
		self.bas = False
		self.droite = False
		self.gauche = False
		for arg in args:
			if arg == 'droite':
				self.droite = True
			elif arg == 'gauche':
				self.gauche = True
			elif arg == 'haut':
				self.haut = True
			elif arg == 'bas':
				self.bas = True

	def __repr__(self):
		return 'haut:{} bas:{} gauche:{} droite:{} '.format(self.haut, self.bas, self.gauche, self.droite)

class Case:
	def __init__(self, x, y, robot, objectif, mur):
		self.x = x
		self.y = y
		self.robot = robot
		self.objectif = objectif
		self.mur = mur

	def __repr__(self):
		if (self.robot.couleur != None and self.objectif.couleur != None):
			return 'x:{} y:{} robot:{} objectif:{} '.format(self.x, self.y, self.robot, self.objectif)
		elif (self.robot.couleur != None):
			return 'x:{} y:{} robot:{} '.format(self.x, self.y, self.robot)
		elif (self.objectif.couleur != None):
			return 'x:{} y:{} objectif:{} '.format(self.x, self.y, self.objectif)
		else:
			return " "

	def crée_mur(self, direction):
		if direction == 'haut':
			self.mur.haut = True
		elif direction == 'bas':
			self.mur.bas = True
		elif direction == 'droite':
			self.mur.droite = True
		else:
			self.mur.gauche = True


class Plateau:
	def __init__(self, taille, précision, centrale=2):
		self.précision = précision
		self.taille = taille
		self.nbrRobot = len(ROBOT)
		self.centrale = centrale
		self.nbrCoin = NBR_COIN
		self.nbrBarre = NBR_BARRE
		self.robots = {couleur: Robot(couleur) for couleur in ROBOT}
		self.couleur_robot = ROBOT
		self.couleur_robot_rgb = COULEUR_ROBOT_RVB
		self.objectif = Objectif()
		self.tableau = [ [ Case(i, j, Robot(), Objectif(), Mur()) for i in range(self.taille)] for j in range(self.taille)]
		self.init = {}	#coordonnées initiales des robots
		self.list_coins = []

	def __repr__(self):
		string = ""
		for i in range (self.taille):
			for j in range (self.taille):
				case = self.tableau[i][j]
				string += str(case)
				string += "\n"
		return string
		

	'''REINITIALISER PLATEAU'''

	def clear(self):
		self.tableau = [ [ Case(i, j, Robot(), Objectif(), Mur()) for i in range(self.taille)] for j in range(self.taille)]
		self.objectif =  Objectif(None, None, None)
		self.list_coins = []


	'''FONCTIONS SUR LES ROBOTS'''

	def coord_init(self):
		for couleur, robot in self.robots.items():
			self.init[couleur] = robot.coord()

	def get_coord_robots(self):
		coord = {}
		for robot in self.robots:
			coord[robot] = self.robots[robot].coord()
		return coord
	
	def placer_robot(self, couleur, x_new, y_new):
		self.tableau[x_new][y_new].robot.couleur = couleur
		self.robots[couleur].x = x_new
		self.robots[couleur].y = y_new
		self.robots[couleur].principal = (couleur == self.objectif.couleur)

	def bouger_robot(self, couleur, x_new, y_new):
		(x_init, y_init) = self.robots[couleur].coord()
		if x_init != None and y_init != None:	#permet de placer le robot s'il n'est pas encore sur le plateau
			self.tableau[x_init][y_init].robot.couleur = None
		self.tableau[x_new][y_new].robot.couleur = couleur
		self.robots[couleur].x = x_new
		self.robots[couleur].y = y_new
		self.robots[couleur].principal = (couleur == self.objectif.couleur)

	def replacer_robots(self, list):
		for robot in self.robots:
			(x_new, y_new) = list[robot]
			self.bouger_robot(robot, x_new, y_new)

	def remove_robot(self, couleur):
		x,y = self.robots[couleur].coord()
		self.tableau[x][y].robot.couleur = None
		self.robots[couleur] = Robot(couleur=couleur)

	def dep(self, couleur, direction):
		(x,y) = self.robots[couleur].coord()
		directions = {'haut': self.dep_haut,
    				  'bas': self.dep_bas,
    				  'droite': self.dep_droite,
    				  'gauche': self.dep_gauche}
		directions[direction](couleur, x, y)

	def dep_haut(self, couleur, a, b):
		if self.tableau[a][b].mur.haut == True or self.tableau[a-1][b].robot.couleur != None:
			return
		i = 1
		while not(self.tableau[a-i][b].mur.haut==True or self.tableau[a-1-i][b].robot.couleur !=None):
			i+=1
		self.bouger_robot(couleur, a-i, b)
		
	def dep_bas(self, couleur, a, b):
		if self.tableau[a][b].mur.bas == True or self.tableau[a+1][b].robot.couleur != None:
			return
		i = 1
		while not(self.tableau[a+i][b].mur.bas==True or self.tableau[a+1+i][b].robot.couleur !=None):
			i+=1
		self.bouger_robot(couleur, a+i, b)

	def dep_droite(self, couleur, a, b):
		if self.tableau[a][b].mur.droite == True or self.tableau[a][b+1].robot.couleur != None:
			return
		i = 1
		while not(self.tableau[a][b+i].mur.droite==True or self.tableau[a][b+1+i].robot.couleur !=None):
			i+=1
		self.bouger_robot(couleur, a, b+i)

	def dep_gauche(self, couleur, a, b):
		if self.tableau[a][b].mur.gauche == True or self.tableau[a][b-1].robot.couleur != None:
			return
		i = 1
		while not(self.tableau[a][b-i].mur.gauche==True or self.tableau[a][b-1-i].robot.couleur !=None):
			i+=1
		self.bouger_robot(couleur, a, b-i)

	'''FONCTIONS SUR L'OBJECTIF'''

	def bouger_obj(self, couleur, x_new, y_new):
		x,y = self.objectif.coord()
		if x != None and y != None:		#permet de placer l'objectif s'il n'est pas encore sur le plateau
			self.tableau[x][y].objectif.couleur = None
		self.objectif = Objectif(x_new, y_new, couleur)
		self.tableau[x_new][y_new].objectif.couleur = couleur
		for robot in self.robots:
			self.robots[robot].principal = False
		self.robots[couleur].principal = True

	'''CONSTRUCTION DU PLATEAU'''

	def ajout_murs(self, direction, x, y):
		self.tableau[x][y].crée_mur(direction)
		if direction == 'haut':
			self.tableau[x-1][y].mur.bas = True
		elif direction == 'bas':
			self.tableau[x+1][y].mur.haut = True
		elif direction == 'droite':
			self.tableau[x][y+1].mur.gauche = True
		else:
			self.tableau[x][y-1].mur.droite = True
	
	def init1(self):	#place les murs sur les bords
		for i in range(self.taille):
			self.tableau[i][0].mur.gauche = True
			self.tableau[i][self.taille-1].mur.droite = True
		for j in range(self.taille):
			self.tableau[0][j].mur.haut = True
			self.tableau[self.taille-1][j].mur.bas = True

	def init2(self):	#place les murs centraux
		d = self.centrale // 2
		for i in range(self.taille//2 - d, self.taille//2 + d):
			self.ajout_murs('gauche', i, self.taille//2 - d)
			self.ajout_murs('droite', i, self.taille//2 + d - 1)
		for j in range(self.taille//2 - d, self.taille//2 + d):
			self.ajout_murs('haut', self.taille//2 - d, j)
			self.ajout_murs('bas', self.taille//2 + d - 1, j)

	def coin_b_d(self, a, b):
		self.ajout_murs('bas', a, b)
		self.ajout_murs('droite', a, b)
		self.list_coins.append(Case(a, b, Robot(), Objectif(), Mur('bas', 'droite')))

	def coin_h_d(self, a, b):
		self.ajout_murs('haut', a, b)
		self.ajout_murs('droite', a, b)
		self.list_coins.append(Case(a, b, Robot(), Objectif(), Mur('haut', 'droite')))

	def coin_b_g(self, a, b):
		self.ajout_murs('bas', a, b)
		self.ajout_murs('gauche', a, b)
		self.list_coins.append(Case(a, b, Robot(), Objectif(), Mur('bas', 'gauche')))

	def coin_h_g(self, a, b):
		self.ajout_murs('haut', a, b)
		self.ajout_murs('gauche', a, b)
		self.list_coins.append(Case(a, b, Robot(), Objectif(), Mur('haut', 'gauche')))

	def coint(self, a,b,c,d,e,f,g,h):
		self.coin_h_g(a,b)
		self.coin_b_d(c,d)
		self.coin_b_g(e,f)
		self.coin_h_d(g,h)

	def barre_v(self, a, b):  # b est à gauche de la barre
		self.ajout_murs('droite', a, b)

	def barrevt(self, L):
		for l in L:
			self.barre_v(l[0],l[1])

	def barre_h(self, a, b):  # a est en haut de la barre
		self.ajout_murs('bas', a, b)
			
	def barreht(self, L):
		for l in L:
			self.barre_h(l[0],l[1])



	'''FONCTIONS SUR LES DISTANCES ENTRE LES CASES'''

	def distance_obstacle_droite(self, a, b, sans_robot=False):       # renvoie la distance entre la case (a,b) et son premier ostacle vers la droite
		i = 0
		while not (self.tableau[a][b+i].mur.droite  or (self.tableau[a][b+1+i].robot.couleur != None and not sans_robot)):
			i += 1
		return i

	def distance_obstacle_gauche(self, a, b, sans_robot=False):      # renvoie la distance entre la case (a,b) et son premier ostacle vers la gauche
		i = 0
		while not (self.tableau[a][b-i].mur.gauche  or (self.tableau[a][b-1-i].robot.couleur != None and not sans_robot)):
			i += 1
		return i

	def distance_obstacle_bas(self, a, b, sans_robot=False):     # renvoie la distance entre la case (a,b) et son premier ostacle vers le bas
		i = 0
		while not (self.tableau[a+i][b].mur.bas  or (self.tableau[a+i+1][b].robot.couleur != None and not sans_robot)):
			i += 1
		return i

	def distance_obstacle_haut(self, a, b, sans_robot=False):        # renvoie la distance entre la case (a,b) et son premier ostacle vers le haut
		i = 0
		while not (self.tableau[a-i][b].mur.haut  or (self.tableau[a-i-1][b].robot.couleur != None and not sans_robot)):
			i += 1
		return i
	
	def tableau_distance(self, x, y, couleur):	#renvoie pour chaque case une liste [distance, [dep1,dep2,...]] pour que le robot 'couleur' arrive sur la case (x,y)
		n = self.taille
		(x_robot, y_robot) = self.robots[couleur].coord()
		self.remove_robot(couleur)	#on enlève le robot
		tab_dis = [[ [ [], []] for i in range(n)] for j in range(n)]
		tab_dis[x][y][0] = 0	#case départ
		compteur2 = 0
		arret  = True
		mem = [(x, y)]
		while arret:
			arret = False
			for i in range(n):
				for j in range(n):
					if tab_dis[i][j][0] == compteur2:

						d = self.distance_obstacle_droite(i, j)
						if self.tableau[i][j].mur.gauche or self.tableau[i][j-1].robot.couleur != None:
							for t in range(1, d+1):
								if (i,j+t) not in mem:
									tab_dis[i][j+t][0] = compteur2 + 1
									tab_dis[i][j+t][1] = [couleur] + ['gauche'] + tab_dis[i][j][1]
									mem.append((i,j+t))
									arret = True
						
						g = self.distance_obstacle_gauche(i, j)
						if self.tableau[i][j].mur.droite or self.tableau[i][j+1].robot.couleur != None:
							for t in range(1, g+1):
								if (i, j-t) not in mem:
									tab_dis[i][j-t][0] = compteur2 + 1
									tab_dis[i][j-t][1] = [couleur] + ['droite'] + tab_dis[i][j][1]
									mem.append((i, j-t))
									arret = True

						h = self.distance_obstacle_haut(i, j)
						if self.tableau[i][j].mur.bas or self.tableau[i+1][j].robot.couleur != None:
							for t in range(1, h+1):
								if ((i-t), j) not in mem:
									tab_dis[i-t][j][0] = compteur2 + 1
									tab_dis[i-t][j][1] = [couleur] + ['bas'] + tab_dis[i][j][1]
									mem.append(((i-t), j))
									arret  = True

						bas = self.distance_obstacle_bas(i, j)
						if self.tableau[i][j].mur.haut or self.tableau[i-1][j].robot.couleur != None:
							for t in range(1, bas+1):
								if ((i+t), j) not in mem:
									tab_dis[i+t][j][0] = compteur2 + 1
									tab_dis[i+t][j][1] = [couleur] + ['haut'] + tab_dis[i][j][1]
									mem.append(((i+t), j))
									arret = True
			compteur2 += 1
		self.bouger_robot(couleur, x_robot, y_robot)  # on remet le robot
		return tab_dis
	
	def tableau_distance_min(self, a, b): # tableau associant à chaque case la distance minimale qui la sépare de la case (a,b)
		n = self.taille
		tab_dis_min = [[ [] for i in range(n)] for j in range(n)]
		tab_dis_min[a][b] = 0
		compteur2 = 0
		mem = [(a, b)] # liste contentant toutes les cases déjà visiter
		while len(mem) <n*n - self.centrale*self.centrale: # on s'arrête quand on est passé sur toutes les cases (sauf les centrales)
			for i in range(n):
				for j in range(n):
					if tab_dis_min[i][j] == compteur2:

						d = self.distance_obstacle_droite(i, j, sans_robot=True)
						for t in range(1, d+1):
							if (i,j+t) not in mem:
								tab_dis_min[i][j+t] = compteur2 + 1
								mem.append((i,j+t))

						g = self.distance_obstacle_gauche(i, j, sans_robot=True)
						for t in range(1, g+1):
							if (i, j-t) not in mem:
								tab_dis_min[i][j-t] = compteur2 + 1
								mem.append((i, j-t))

						h = self.distance_obstacle_haut(i, j, sans_robot=True)
						for t in range(1, h+1):
							if ((i-t), j) not in mem:
								tab_dis_min[i-t][j] = compteur2 + 1
								mem.append(((i-t), j))

						bas = self.distance_obstacle_bas(i, j, sans_robot=True)
						for t in range(1, bas+1):
							if ((i+t), j) not in mem:
								tab_dis_min[i+t][j] = compteur2 + 1
								mem.append(((i+t), j))
			compteur2 += 1
		return tab_dis_min
	
	
	'''AFFICHAGE DU PLATEAU'''

	def affich(self):
		m = self.taille * self.précision
		a = 255 * np.ones((m, m, 3), dtype=np.uint8)
		for i in range (m):
			for j in range (m):
				if i%self.précision==0 or j%self.précision==0:
					a[i][j]=[200,200,200] # colorie le quadrillage en gris
		a[(self.taille-self.centrale)//2*self.précision:(self.taille+self.centrale)//2*self.précision,(self.taille-self.centrale)//2*self.précision:(self.taille+self.centrale)//2*self.précision]=[1,1,1] #colorie le carré central en noir
		
		if self.objectif != None:
			i, j = self.objectif.x, self.objectif.y
			for rang, robot in enumerate(self.couleur_robot):
				if robot == self.objectif.couleur:
					a[self.précision*i+1:self.précision*i+self.précision,self.précision*j+1:self.précision*j+self.précision] = self.couleur_robot_rgb[rang]		# colorie l'objectif
					a[self.précision*i+1+(self.précision//5):self.précision*i+self.précision-(self.précision//5),self.précision*j+1+(self.précision//5):self.précision*j+self.précision-(self.précision//5)] = [255,255,255]
		for rang, robot in enumerate(self.couleur_robot):
			x,y = self.robots[robot].coord()
			if (str(self.tableau[x][y].robot) == "robot:None"):
				print("ATTENTION PLATEAU VIDE DE ROBOTS")
			a[self.précision*x+1:self.précision*x+self.précision,self.précision*y+1:self.précision*y+self.précision] = self.couleur_robot_rgb[rang]

		for i in range (self.taille):
			for j in range (self.taille):
				if self.tableau[i][j].mur.haut:
					a[self.précision*i,self.précision*j:self.précision*j+self.précision,:]=0 #colorie les murs 1 en noir
				if self.tableau[i][j].mur.droite:
					a[self.précision*i:self.précision*i+self.précision,self.précision*j+self.précision-1,:]=0 #colorie les murs 2 en noir
				if self.tableau[i][j].mur.bas:
					a[self.précision*i+self.précision-1,self.précision*j:self.précision*j+self.précision,:]=0 #colorie les murs 3 en noir
				if self.tableau[i][j].mur.gauche:
					a[self.précision*i:self.précision*i+self.précision,self.précision*j,:]=0 #colorie les murs 4 en noir
		# plt.figure(figsize=(6, 6))
		# plt.imshow(a, interpolation='nearest')
		# plt.axis('off')
		# plt.show()
		return a
	
	
	'''GENERATION PLATEAU ALEATOIRE'''

	def random(self):	#crée une grille aléatoire (avec coins, barres, robots et objectif)
		self.clear()
		self.init1()
		self.init2()
		max = self.taille-1         # max = 15
		moitie = self.taille//2 - 1 # moitie = 7

		for k in range(self.nbrBarre):
			vert1, vert2, vert3, vert4, hor1, hor2, hor3, hor4 =  self.rand_barres()
			self.barrevt([[0,vert1],[0,vert2+moitie],[max,vert3],[max,vert4+moitie]]) # place les barres verticales
			self.barreht([[hor1,0],[hor2+moitie,0],[hor3,max],[hor4+moitie,max]]) # place les barres horizontales

		L = [[0,0], [0,moitie], [moitie,0], [moitie,moitie]]  # liste qui permet de placer les coins à la bonne place dans chaque quart de plateau
		for k in range(self.nbrCoin):
			for t in range (4):
				x_coin1,y_coin1,x_coin2,y_coin2,x_coin3,y_coin3,x_coin4,y_coin4 = self.rand_coin(t,L, moitie)
				while (self.grille_pas_bonne(x_coin1,y_coin1,x_coin2,y_coin2,x_coin3,y_coin3,x_coin4,y_coin4) and k != 1):
					x_coin1,y_coin1,x_coin2,y_coin2,x_coin3,y_coin3,x_coin4,y_coin4 = self.rand_coin(t,L, moitie)
				self.coin_b_d(x_coin1, y_coin1)
				self.coin_b_g(x_coin2, y_coin2)
				self.coin_h_d(x_coin3, y_coin3)
				self.coin_h_g(x_coin4, y_coin4)
		self.rand_obj()
		self.place_robot_random()
		# self.coord_init()	#stock les coordonnées initiales des robots
		
	def place_robot_random(self):
		max = self.taille - 1
		for robot in self.robots:       # place les robots
			x,y = randint(1, max), randint(1, max)
			while (self.tableau[x][y].robot.couleur != None or self.central(x,y) or not self.case_pas_emmurée(x, y)): # si la case est vide et non centrale
				x,y = randint(1, max), randint(1, max)
			self.bouger_robot(robot, x, y) #place le robot
		self.coord_init()	#stock les coordonnées initiales des robots

	def rand_barres(self):	# renvoie les coordonnées des barres verticales et horizontales (1 barre vert et hor / quart de plateau)
		nmax = (self.taille-self.centrale)//2 -1
		vert1, vert2, vert3, vert4, hor1, hor2, hor3, hor4=randint(1,nmax),randint(1,nmax),randint(1,nmax),randint(1,nmax),randint(1,nmax),randint(1,nmax),randint(1,nmax),randint(1,nmax)
		return vert1, vert2, vert3, vert4, hor1, hor2, hor3, hor4
	
	def rand_coin(self, t, L, moitie): # renvoie les coordonnées des 4 coins du quart de plateau
		[A,C,E,G] , [B,D,F,H] = sample([i for i in range(1,moitie)],4),sample([i for i in range(1,moitie)],4) # tire deux quadruplet de nombre différents pour avoir les 4 coordonnées des coins et qu'ils ne soient pas alignés
		return A+L[t][0],B+L[t][1],C+L[t][0],D+L[t][1],E+L[t][0],F+L[t][1],G+L[t][0],H+L[t][1] 

	def rand_obj(self):
		x, y = self.objectif.coord()
		if (x != None and y != None):
			print("x= ", x, " y= ", y)
			self.tableau[x][y].objectif.couleur = None
		liste_coins = []
		coin_choisi_pas_bloqué = False
		for coin in self.list_coins:
			if self.objectif.x == None or (coin.x != self.objectif.x and coin.y != self.objectif.y):
				liste_coins.append([coin.x, coin.y])
		while(not coin_choisi_pas_bloqué):	# évite que l'objectif soit emmuré
			coin_choisi, couleur_obj = sample(liste_coins, 1)[0], sample(self.couleur_robot, 1)[0]
			if (self.case_pas_emmurée(coin_choisi[0], coin_choisi[1]) and not self.central(coin_choisi[0], coin_choisi[1])):
				coin_choisi_pas_bloqué = True
		self.tableau[coin_choisi[0]][coin_choisi[1]].objectif.couleur = couleur_obj
		self.objectif = Objectif(coin_choisi[0], coin_choisi[1], couleur_obj)
		self.robots[couleur_obj].principal = True

	def case_pas_emmurée(self, x, y):	# renvoie True si la case n'est pas emmurée
		return (self.tableau[x][y].mur.haut != True or self.tableau[x][y].mur.bas != True or self.tableau[x][y].mur.droite != True or self.tableau[x][y].mur.gauche != True)

	def grille_pas_bonne(self, a,b,c,d,e,f,g,h): # renvoie True si la grille n'est pas bonne, c'est à dire que les coins sont adjacents ou que les coins sont adjacents à un mur
		return (self.autour(a,b,c,d) or self.autour(a,b,e,f) or self.autour(a,b,g,h) or self.autour(c,d,e,f) or self.autour(c,d,g,h) or self.autour(e,f,g,h)) or (self.voisin(a,b) or self.voisin(c,d) or self.voisin(e,f) or self.voisin(g,h))

	def voisin(self,i,j):	# renvoie True si la case (i,j) a un mur adjacent
		return (self.tableau[i][j].mur.haut or self.tableau[i][j].mur.bas or self.tableau[i][j-1].mur.haut or self.tableau[i][j-1].mur.bas or self.tableau[i][j+1].mur.haut or self.tableau[i][j+1].mur.bas or self.tableau[i-1][j].mur.droite or self.tableau[i-1][j].mur.gauche or self.tableau[i+1][j].mur.droite or self.tableau[i+1][j].mur.gauche)
	
	def autour(self, x1,y1,x2,y2): # renvoie True si la case (x1,y1) est située dans les 8 cases autour de celle (x2,y2) (ou même case)
		return (x2>=x1-1 and x2<=x1+1) and (y2>=y1-1 and y2<=y1+1)

	def central(self, x,y):
		a, b = (self.taille-self.centrale)//2, (self.taille+self.centrale)//2
		l=[(i,j) for i in range(a,b) for j in range(a,b)]
		for k in l:
			if (x,y)==(k):
				return True
		return False
	
	'''GENERATION PLATEAU REEL'''

	def generer_vrai_plateau(self, Quadrant_h_g, Quadrant_h_d, Quadrant_b_g, Quadrant_b_d):
		self.clear()
		# for i in range (self.taille):
		# 	for j in range (self.taille):
		# 		case = self.tableau[i][j]
		# 		print(case)
		self.init1()
		self.init2()
		Quadrants = [Quadrant_h_g, Quadrant_h_d, Quadrant_b_g, Quadrant_b_d]
		for Quadrant in Quadrants:
			for coin_h_g in Quadrant.get_list_coin_h_g():
				self.coin_h_g(coin_h_g[0], coin_h_g[1])
			for coin_h_d in Quadrant.get_list_coin_h_d():
				self.coin_h_d(coin_h_d[0], coin_h_d[1])
			for coin_b_g in Quadrant.get_list_coin_b_g():
				self.coin_b_g(coin_b_g[0], coin_b_g[1])
			for coin_b_d in Quadrant.get_list_coin_b_d():
				self.coin_b_d(coin_b_d[0], coin_b_d[1])
			for bv in Quadrant.get_list_bv():
				self.barre_v(bv[0], bv[1])
			for bh in Quadrant.get_list_bh():
				self.barre_h(bh[0], bh[1])
		self.rand_obj()
		self.place_robot_random()
		# self.coord_init()	#stock les coordonnées initiales des robots

	'''RESOLUTION PLATEAU'''

	def est_resolu(self):	#renvoie True si le bon robot est sur l'objectif
		return self.objectif.coord() == self.robots[self.objectif.couleur].coord()
	
	
	'''OUVERTURE CARTE FICHER TEXTE'''	

	def open(self, name):
		self.clear()
		self.init1()
		self.init2()
		
		path = '../Map/%s.txt' % name
		with open(path, 'r') as f:
			lines = f.read().splitlines()
		
		section = None

		for line in lines:
			line = line.strip()
			if not line or line.startswith("#"):
				continue

			parts = line.split()
			if not parts:
				continue

			key = parts[0]
			value = parts[1:] if len(parts) > 1 else []

			if key in {
				"TAILLE_DE_LA_GRILLE", "TAILLE_CASE_CENTRALE",
				"OBJECTIF", "NOMBRE_ROBOTS",
				"COIN_HAUT_DROIT", "COIN_HAUT_GAUCHE", "COIN_BAS_DROIT", "COIN_BAS_GAUCHE",
				"BARRE_VERTICALE", "BARRE_HORIZONTALE"
			}:
				section = key
				# Gérer les sections à valeur directe sur la même ligne
				if section == "TAILLE_DE_LA_GRILLE" and value:
					self.taille = int(value[0])
					self.clear()
					self.init1()
				elif section == "TAILLE_CASE_CENTRALE" and value:
					self.centrale = int(value[0])
					self.init2()
				elif section == "NOMBRE_ROBOTS" and value:
					self.nbrRobot = int(value[0])
					self.couleur_robot_rgb = generate_distinct_colors(self.nbrRobot)
					self.couleur_robot = [convert_rgb_to_names(tuple) for tuple in self.couleur_robot_rgb]
					self.robots = {couleur: Robot(couleur) for couleur in self.couleur_robot}
				continue

			# Section OBJECTIF (plusieurs lignes)
			if section == "OBJECTIF":
				if len(parts) == 3 and parts[0].startswith("obj"):
					idx = int(parts[0][3:])
					i, j = int(parts[1]), int(parts[2])
					couleur = self.couleur_robot[idx]
					self.bouger_obj(couleur, i, j)
				continue

			# Section robots (ex: rob0 10 6)
			if parts[0].startswith("rob") and len(parts) == 3:
				idx = int(parts[0][3:])
				i, j = int(parts[1]), int(parts[2])
				couleur = self.couleur_robot[idx]
				self.bouger_robot(couleur, i, j)
				continue

			# Sections des coins
			if section in ("COIN_HAUT_DROIT", "COIN_HAUT_GAUCHE", "COIN_BAS_DROIT", "COIN_BAS_GAUCHE"):
				if len(parts) == 2:
					i, j = int(parts[0]), int(parts[1])
					mur = None
					if section == "COIN_HAUT_DROIT":
						self.coin_h_d(i, j)
						mur = Mur('haut', 'droite')
					elif section == "COIN_HAUT_GAUCHE":
						self.coin_h_g(i, j)
						mur = Mur('haut', 'gauche')
					elif section == "COIN_BAS_DROIT":
						self.coin_b_d(i, j)
						mur = Mur('bas', 'droite')
					elif section == "COIN_BAS_GAUCHE":
						self.coin_b_g(i, j)
						mur = Mur('bas', 'gauche')
					if mur:
						self.list_coins.append(Case(i, j, Robot(), Objectif(), mur))
				continue

			# Barres verticales
			if section == "BARRE_VERTICALE":
				if len(parts) == 2:
					i, j = int(parts[0]), int(parts[1])
					self.barre_v(i, j)
				continue

			# Barres horizontales
			if section == "BARRE_HORIZONTALE":
				if len(parts) == 2:
					i, j = int(parts[0]), int(parts[1])
					self.barre_h(i, j)
				continue

		self.coord_init()



	def open_old(self, name):
		self.clear()
		self.init1()
		self.init2()
		path = '../OldMap/%s.txt' % (name)
		with open (path, 'r') as f:
			lines = f.read().splitlines()
		lines2 = [l.split(',') for l in lines]
		for l in lines2:
			i, j = int(l[1]), int(l[2])
			for rang, robot in enumerate(self.couleur_robot):
				if l[0] == 'r%d' % (rang):
					self.bouger_robot(robot, i, j)	#add robot
				if l[0] == 'o%d' % (rang):
					self.bouger_obj(robot, i, j)	#add objective
			if l[0] == 'bv':
				self.barre_v(i,j)
			if l[0] == 'bh':
				self.barre_h(i,j)
			if l[0] == 'c1' or l[0] == 'c_h_d':
				self.coin_h_d(i,j)
				self.list_coins.append(Case(i, j, Robot(), Objectif(), Mur('haut', 'droite')))
			if l[0] == 'c2'or l[0] == 'c_b_d':
				self.coin_b_d(i,j)
				self.list_coins.append(Case(i, j, Robot(), Objectif(), Mur('bas', 'droite')))
			if l[0] == 'c3'or l[0] == 'c_b_g':
				self.coin_b_g(i,j)
				self.list_coins.append(Case(i, j, Robot(), Objectif(), Mur('bas', 'gauche')))
			if l[0] == 'c4'or l[0] == 'c_h_g':
				self.coin_h_g(i,j)
				self.list_coins.append(Case(i, j, Robot(), Objectif(), Mur('haut', 'gauche')))
		self.coord_init()

class Quadrant:
	def __init__(self, forme, index): # forme de l'objectif vert et recto ou verso (recto = plus proche de la case origine)
		self.forme = forme
		self.index = index
		self.list_coin_h_g = []
		self.list_coin_h_d = []
		self.list_coin_b_d = []
		self.list_coin_b_g = []
		self.list_bv = []
		self.list_bh = []

		if self.forme == "étoile":
			if self.index == "recto":
				self.list_coin_h_g.append([4, 6])
				self.list_coin_h_d.append([6, 2])
				self.list_coin_b_g.append([2, 1])
				self.list_coin_b_d.append([1, 5])
				self.list_bv.append([0, 3])
				self.list_bh.append([3, 0])
			elif self.index == "verso":
				self.list_coin_h_g.append([1, 2])
				self.list_coin_h_d.append([5, 4])
				self.list_coin_b_g.append([3, 6])
				self.list_coin_b_d.append([6, 1])
				self.list_bv.append([0, 4])
				self.list_bh.append([4, 0])

		elif self.forme == "lune":
			if self.index == "recto":
				self.list_coin_h_g.append([6, 1])
				self.list_coin_h_d.append([4, 2])
				self.list_coin_b_g.append([5, 7])
				self.list_coin_b_d.append([2, 5])
				self.list_bv.append([0, 3])
				self.list_bh.append([4, 0])
			elif self.index == "verso":
				self.list_coin_h_g.append([5, 7])
				self.list_coin_h_d.append([2, 6])
				self.list_coin_b_g.append([1, 1])
				self.list_coin_b_d.append([4, 2])
				self.list_bv.append([0, 3])
				self.list_bh.append([5, 0])

		elif self.forme == "planète":
			if self.index == "recto":
				self.list_coin_h_g.append([4, 6])
				self.list_coin_h_d.append([6, 5])
				self.list_coin_b_g.append([3, 1])
				self.list_coin_b_d.append([1, 2])
				self.list_coin_b_d.append([7, 3])
				self.list_bv.append([0, 4])
				self.list_bh.append([4, 0])
			elif self.index == "verso":
				self.list_coin_h_g.append([4, 5])
				self.list_coin_h_d.append([3, 1])
				self.list_coin_b_g.append([1, 6])
				self.list_coin_b_d.append([5, 2])
				self.list_coin_b_d.append([5, 7])
				self.list_bv.append([0, 3])
				self.list_bh.append([6, 0])

		elif self.forme == "soleil":
			if self.index == "recto":
				self.list_coin_h_g.append([1, 4])
				self.list_coin_h_d.append([2, 1])
				self.list_coin_b_g.append([6, 3])
				self.list_coin_b_d.append([3, 6])
				self.list_bv.append([0, 1])
				self.list_bh.append([4, 0])
			elif self.index == "verso":
				self.list_coin_h_g.append([2, 1])
				self.list_coin_h_d.append([5, 6])
				self.list_coin_b_g.append([6, 3])
				self.list_coin_b_d.append([1, 6])
				self.list_bv.append([0, 4])
				self.list_bh.append([5, 0])

	def __repr__(self):
		return (f"Quadrant(forme={self.forme}, index={self.index},\n"
                f"  list_coin_h_g={self.list_coin_h_g},\n"
                f"  list_coin_h_d={self.list_coin_h_d},\n"
                f"  list_coin_b_d={self.list_coin_b_d},\n"
                f"  list_coin_b_g={self.list_coin_b_g},\n"
                f"  list_bv={self.list_bv},\n"
                f"  list_bh={self.list_bh})")

	# Getter pour forme
	def get_forme(self):
		return self.forme

	# Getter pour index
	def get_index(self):
		return self.index

	# Getters pour les coins
	def get_list_coin_h_g(self):
		return self.list_coin_h_g

	def get_list_coin_h_d(self):
		return self.list_coin_h_d

	def get_list_coin_b_d(self):
		return self.list_coin_b_d

	def get_list_coin_b_g(self):
		return self.list_coin_b_g

	# Getters pour les barres
	def get_list_bv(self):
		return self.list_bv

	def get_list_bh(self):
		return self.list_bh

	def rotate_point_right(self, point):
		# Rotation 90° à droite : (x, y) -> (y, 7 - x)
		return [point[1], 15 - point[0]]
	
	def rotate_point_left(self, point):
		# Rotation 90° à gauche : (x, y) -> (7 - y, x)
		return [15 - point[1], point[0]]
	
	def rotate_coins_right(self):
		# Rotation des coins : ajustement en fonction de la rotation
		# h_g -> h_d, h_d -> b_d, b_d -> b_g, b_g -> h_g
		self.list_coin_h_d, self.list_coin_b_d, self.list_coin_b_g, self.list_coin_h_g = self.list_coin_h_g, self.list_coin_h_d, self.list_coin_b_d, self.list_coin_b_g

	def rotate_coins_left(self):
		# Rotation des coins : ajustement en fonction de la rotation
		# h_g -> b_g, h_d -> h_g, b_d -> h_d, b_g -> b_d
		self.list_coin_b_g, self.list_coin_h_g, self.list_coin_h_d, self.list_coin_b_d = self.list_coin_h_g, self.list_coin_h_d, self.list_coin_b_d, self.list_coin_b_g

	def rotate_bv_bh(self):
		# Rotation des barres : les barres verticales deviennent horizontales et inversement
		self.list_bv, self.list_bh = self.list_bh, self.list_bv

	def rotation_90_degre_droite(self):
		# Rotation des coins
		self.list_coin_h_g = [self.rotate_point_right(p) for p in self.list_coin_h_g]
		self.list_coin_h_d = [self.rotate_point_right(p) for p in self.list_coin_h_d]
		self.list_coin_b_g = [self.rotate_point_right(p) for p in self.list_coin_b_g]
		self.list_coin_b_d = [self.rotate_point_right(p) for p in self.list_coin_b_d]

		# Rotation des autres éléments
		self.list_bv = [self.rotate_point_right(p) for p in self.list_bv]
		self.list_bh = [self.rotate_point_right(p) for p in self.list_bh]
		for p in self.list_bh:
			p[1] -= 1

		# Appliquer les ajustements pour les coins et les barres
		self.rotate_coins_right()
		self.rotate_bv_bh()

		return self

	def rotation_90_degre_gauche(self):
		# Rotation des coins
		self.list_coin_h_g = [self.rotate_point_left(p) for p in self.list_coin_h_g]
		self.list_coin_h_d = [self.rotate_point_left(p) for p in self.list_coin_h_d]
		self.list_coin_b_g = [self.rotate_point_left(p) for p in self.list_coin_b_g]
		self.list_coin_b_d = [self.rotate_point_left(p) for p in self.list_coin_b_d]

		# Rotation des autres éléments
		self.list_bv = [self.rotate_point_left(p) for p in self.list_bv]
		for p in self.list_bv:
			p[0] -= 1
		self.list_bh = [self.rotate_point_left(p) for p in self.list_bh]

		# Appliquer les ajustements pour les coins et les barres
		self.rotate_coins_left()
		self.rotate_bv_bh()

		return self
	
import os
from collections import defaultdict

def convertir_fichier(input_path, output_path):
    robots_pos = {}
    objectifs_pos = {}
    murs_coin = defaultdict(list)
    murs_bv = []
    murs_bh = []

    max_coord = 0

    with open(input_path, 'r') as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            parts = line.split(',')
            code = parts[0]
            i, j = int(parts[1]), int(parts[2])
            max_coord = max(max_coord, i, j)

            if code.startswith('r'):
                idx = int(code[1:])
                robots_pos[idx] = (i, j)
            elif code.startswith('o'):
                idx = int(code[1:])
                objectifs_pos[idx] = (i, j)
            elif code == 'bv':
                murs_bv.append((i, j))
            elif code == 'bh':
                murs_bh.append((i, j))
            elif code in ('c_h_d', 'c1'):
                murs_coin['COIN_HAUT_DROIT'].append((i, j))
            elif code in ('c_b_d', 'c2'):
                murs_coin['COIN_BAS_DROIT'].append((i, j))
            elif code in ('c_b_g', 'c3'):
                murs_coin['COIN_BAS_GAUCHE'].append((i, j))
            elif code in ('c_h_g', 'c4'):
                murs_coin['COIN_HAUT_GAUCHE'].append((i, j))

    taille_grille = max_coord + 1
    taille_case_centrale = 2

    lines = []
    lines.append(f"TAILLE_DE_LA_GRILLE {taille_grille}")
    lines.append(f"TAILLE_CASE_CENTRALE {taille_case_centrale}")

    lines.append("OBJECTIF")
    for idx in sorted(objectifs_pos.keys()):
        i, j = objectifs_pos[idx]
        lines.append(f"obj{idx} {i} {j}")

    lines.append(f"NOMBRE_ROBOTS {len(robots_pos)}")
    for idx in sorted(robots_pos.keys()):
        i, j = robots_pos[idx]
        lines.append(f"rob{idx} {i} {j}")

    for coin_type in ['COIN_HAUT_DROIT', 'COIN_HAUT_GAUCHE', 'COIN_BAS_DROIT', 'COIN_BAS_GAUCHE']:
        if murs_coin[coin_type]:
            lines.append(coin_type)
            for i, j in murs_coin[coin_type]:
                lines.append(f"{i} {j}")

    if murs_bv:
        lines.append("BARRE_VERTICALE")
        for i, j in murs_bv:
            lines.append(f"{i} {j}")

    if murs_bh:
        lines.append("BARRE_HORIZONTALE")
        for i, j in murs_bh:
            lines.append(f"{i} {j}")

    # Crée dossier de sortie si besoin
    os.makedirs(os.path.dirname(output_path), exist_ok=True)

    with open(output_path, 'w') as f:
        f.write('\n'.join(lines))

    print(f"Converti {input_path} → {output_path}")

def convertir_tous_les_fichiers():
    dossier_entree = '../Map'
    dossier_sortie = '../MapNew'
    for filename in os.listdir(dossier_entree):
        if filename.endswith('.txt') and not filename.startswith('.'):
            input_path = os.path.join(dossier_entree, filename)
            output_path = os.path.join(dossier_sortie, filename)
            convertir_fichier(input_path, output_path)



if __name__=='__main__':
	T = Plateau(16, 10)
	Q1 = Quadrant("lune", "verso")
	Q2 = Quadrant("étoile", "verso")
	Q2.rotation_90_degre_droite()
	print(Q2)
	Q3 = Quadrant("planète", "verso")
	Q3.rotation_90_degre_gauche()
	print(Q3)
	Q4 = Quadrant("soleil", "verso")
	Q4.rotation_90_degre_gauche().rotation_90_degre_gauche()
	T.generer_vrai_plateau(Q1, Q2, Q3, Q4)
	# T.open('3dep')
	# tab_dis = T.tableau_distance(2,2,"red")
	T.affich()