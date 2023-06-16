import Compagnie from "./Compagnie";
import Dossier from "./Dossier";

export default interface Fichier {
    id: number;
    nom: string;
    fullPath: string;
    racine : Dossier;
    compagnie: Compagnie;
    taille: number;
    extension: string;
}

