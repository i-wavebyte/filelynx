import Compagnie from "./Compagnie";
import Fichier from "./Fichier";

export default interface Dossier {
    id: number;
    nom: string;
    racine: Dossier;
    compagnie: Compagnie;
    dossiers: Dossier[];
    fichiers: Fichier[];
    fullPath: string;

}
