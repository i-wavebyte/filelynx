import Compagnie from "./Compagnie"

export default interface Groupe {
  nom: string,
  membres: string[]
  quota: number
  compagne: Compagnie
}
