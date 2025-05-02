interface Cell {
  text: string;
  playable: boolean;
  x: number;
  y: number;
}
interface GameState {
  cells: Cell[];
  currentPlayer: string;
  winner: string | null;
}
export type { GameState, Cell };