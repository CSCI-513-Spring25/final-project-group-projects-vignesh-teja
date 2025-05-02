import React, { useEffect, useState } from 'react';
import './App.css';

const GRID_SIZE = 20;
const CELL_SIZE = 100;

interface GameState {
  ship: number[];
  pirates: number[][];
  monsters: number[][];
  treasure: number[];
  islands: number[][];
  whirlpools: number[][];
  gameOver: string;
  freezeUses: number;
}

const App: React.FC = () => {
  const [gameState, setGameState] = useState<GameState | null>(null);
  const [isGameOver, setIsGameOver] = useState(false);
  const [isExited, setIsExited] = useState(false);
  const [isStarted, setIsStarted] = useState(false);
  const [selectedPirateIndex, setSelectedPirateIndex] = useState<number | null>(null);

  useEffect(() => {
    if (isStarted && !isExited) {
      fetchGameState();
    }
  }, [isStarted, isExited]);

  useEffect(() => {
    const handleKeyPress = (e: KeyboardEvent) => {
      if (!isStarted || isGameOver || isExited) return;

      if (['ArrowRight', 'ArrowLeft', 'ArrowUp', 'ArrowDown'].includes(e.key)) {
        e.preventDefault();
      }

      let direction: string | null = null;
      switch (e.key) {
        case 'ArrowRight': direction = 'right'; break;
        case 'ArrowLeft': direction = 'left'; break;
        case 'ArrowUp': direction = 'up'; break;
        case 'ArrowDown': direction = 'down'; break;
      }
      if (direction) {
        fetch(`/move?direction=${direction}`).then(fetchGameState);
      }
    };

    window.addEventListener('keydown', handleKeyPress);
    return () => window.removeEventListener('keydown', handleKeyPress);
  }, [isStarted, isGameOver, isExited]);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (selectedPirateIndex !== null) {
        const target = e.target as HTMLElement;
        if (!target.closest('.strategy-overlay') && !target.closest('.pirate')) {
          setSelectedPirateIndex(null);
        }
      }
    };

    document.addEventListener('click', handleClickOutside);
    return () => document.removeEventListener('click', handleClickOutside);
  }, [selectedPirateIndex]);

  const fetchGameState = () => {
    fetch('/state')
      .then(res => res.json())
      .then(data => {
        setGameState(data);
        setIsExited(false);
        if (data.gameOver === 'win' || data.gameOver === 'lose') {
          setIsGameOver(true);
        }
      });
  };

  const resetGame = () => {
    fetch('/reset', { method: 'POST' })
      .then(() => {
        setIsGameOver(false);
        setIsExited(false);
        setIsStarted(true);
        fetchGameState();
      });
  };

  const handleFreeze = () => {
    fetch('/use-freeze?action=activate')
      .then(fetchGameState);
  };

  const handleExit = () => {
    setGameState(null);
    setIsGameOver(false);
    setIsExited(true);
    setIsStarted(false);
  };

  const handleStartGame = () => {
    setIsStarted(true);
  };

  const handleChangeStrategy = (pirateIndex: number, strategy: string) => {
    fetch(`/change-strategy?pirateIndex=${pirateIndex}&strategy=${strategy}`)
      .then(() => {
        console.log(`Pirate ${pirateIndex} strategy changed to ${strategy}`);
        setSelectedPirateIndex(null);
      });
  };

  const handleAddElement = (elementType: string) => {
    fetch(`/add-element?type=${elementType}`)
      .then(() => fetchGameState());
  };

  const handlePirateClick = (index: number) => {
    if (isGameOver) return;
    setSelectedPirateIndex(index === selectedPirateIndex ? null : index);
  };

  const renderStaticGrid = () => {
    const grid = [];
    for (let y = 0; y < GRID_SIZE; y++) {
      for (let x = 0; x < GRID_SIZE; x++) {
        const style = {
          left: x * CELL_SIZE,
          top: y * CELL_SIZE,
          width: CELL_SIZE,
          height: CELL_SIZE,
          position: 'absolute' as const,
        };
        if (gameState?.islands.some(([ix, iy]) => ix === x && iy === y)) {
          grid.push(<img src="/island.png" alt="Island" className="grid-item island" style={style} key={`island-${x}-${y}`} />);
        } else {
          grid.push(<div className="water" style={style} key={`water-${x}-${y}`} />);
        }
      }
    }
    return grid;
  };

  const renderDynamicElements = () => {
    if (!gameState) return null;
    const elements = [];

    const shipStyle = {
      left: gameState.ship[0] * CELL_SIZE,
      top: gameState.ship[1] * CELL_SIZE,
      width: CELL_SIZE,
      height: CELL_SIZE,
      position: 'absolute' as const,
    };
    elements.push(<img src="/ship.png" alt="Ship" className="grid-item ship" style={shipStyle} key="ship" />);

    gameState.pirates.forEach(([px, py], index) => {
      const pirateStyle = {
        left: px * CELL_SIZE,
        top: py * CELL_SIZE,
        width: CELL_SIZE,
        height: CELL_SIZE,
        position: 'absolute' as const,
      };
      elements.push(
        <img
          src="/pirateShip.png"
          alt="Pirate Ship"
          className="grid-item pirate"
          style={pirateStyle}
          onClick={() => handlePirateClick(index)}
          key={`pirate-${index}`}
        />
      );

      if (selectedPirateIndex === index) {
        const overlayStyle = {
          left: px * CELL_SIZE,
          top: (py * CELL_SIZE) - 25,
          width: 300,
          position: 'absolute' as const,
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          padding: '7px',
          borderRadius: '5px',
          display: 'flex',
          gap: '12px',
          justifyContent: 'center',
          zIndex: 5,
        };
        elements.push(
          <div className="strategy-overlay" style={overlayStyle} key={`overlay-${index}`}>
            <button
              onClick={() => handleChangeStrategy(index, 'Chase')}
              style={{
                fontSize: '45px',
                padding: '2px 5px',
                backgroundColor: '#FF4500',
                color: 'white',
                border: 'none',
                borderRadius: '3px',
                cursor: 'pointer',
              }}
            >
              Chase
            </button>
            <button
              onClick={() => handleChangeStrategy(index, 'Patrol')}
              style={{
                fontSize: '45px',
                padding: '2px 5px',
                backgroundColor: '#4682B4',
                color: 'white',
                border: 'none',
                borderRadius: '3px',
                cursor: 'pointer',
              }}
            >
              Patrol
            </button>
          </div>
        );
      }
    });

    gameState.monsters.forEach(([mx, my], index) => {
      const monsterStyle = {
        left: mx * CELL_SIZE,
        top: my * CELL_SIZE,
        width: CELL_SIZE,
        height: CELL_SIZE,
        position: 'absolute' as const,
      };
      elements.push(<img src="/seaMonster.png" alt="Sea Monster" className="grid-item monster" style={monsterStyle} key={`monster-${index}`} />);
    });

    const treasureStyle = {
      left: gameState.treasure[0] * CELL_SIZE,
      top: gameState.treasure[1] * CELL_SIZE,
      width: CELL_SIZE,
      height: CELL_SIZE,
      position: 'absolute' as const,
    };
    elements.push(<img src="/treasure.png" alt="Treasure" className="grid-item treasure" style={treasureStyle} key="treasure" />);

    gameState.whirlpools.forEach(([wx, wy], index) => {
      const whirlpoolStyle = {
        left: wx * CELL_SIZE,
        top: wy * CELL_SIZE,
        width: CELL_SIZE,
        height: CELL_SIZE,
        position: 'absolute' as const,
      };
      elements.push(<img src="/whirlpool.png" alt="Whirlpool" className="grid-item whirlpool" style={whirlpoolStyle} key={`whirlpool-${index}`} />);
    });

    return elements;
  };

  if (!isStarted) {
    return (
      <div className="App">
        <div className="start-screen">
          <h1 className="start-title">⚓ Christopher Columbus Adventure ⚓</h1>
          <p className="start-subtitle">Embark on a daring journey across the seas!</p>
          <div className="ship-container">
            <img src="/ship1.png" alt="Ship 1" className="animated-ship ship1" />
            <img src="/ship2.png" alt="Ship 2" className="animated-ship ship2" />
            <img src="/wave.png" alt="Wave" className="animated-wave" />
          </div>
          <button className="start-game-button" onClick={handleStartGame}>
            Start Your Adventure!
          </button>
        </div>
      </div>
    );
  }

  if (isExited) {
    return (
      <div className="App">
        <h1>Christopher Columbus Adventure</h1>
        <div className="exit-message">Game Exited</div>
        <button className="play-again-button" onClick={resetGame}>
          Start New Game
        </button>
      </div>
    );
  }

  return (
    <div className="App">
      <h1>Christopher Columbus Adventure</h1>
      <div>Freeze Uses Left: {gameState?.freezeUses ?? 2}</div>
      <div className="button-container" style={{ flexWrap: 'wrap' }}>
        <button
          className="add-element-button"
          onClick={handleFreeze}
          disabled={gameState?.freezeUses === 0 || isGameOver}
        >
          Use Freeze
        </button>
        <button
          className="play-again-button"
          onClick={resetGame}
          disabled={isGameOver}
        >
          Reset Game
        </button>
        <button
          className="add-element-button"
          onClick={() => handleAddElement('ChasePirate')}
          disabled={isGameOver}
        >
          Add Chase Pirate
        </button>
        <button
          className="add-element-button"
          onClick={() => handleAddElement('PatrolPirate')}
          disabled={isGameOver}
        >
          Add Patrol Pirate
        </button>
        <button
          className="add-element-button"
          onClick={() => handleAddElement('SeaMonster')}
          disabled={isGameOver}
        >
          Add Sea Monster
        </button>
        <button
          className="add-element-button"
          onClick={() => handleAddElement('Island')}
          disabled={isGameOver}
        >
          Add Island
        </button>
        <button
          className="add-element-button"
          onClick={() => handleAddElement('Whirlpool')}
          disabled={isGameOver}
        >
          Add Whirlpool
        </button>
      </div>
      <div className="game-container">
        <div className="grid" style={{ width: GRID_SIZE * CELL_SIZE, height: GRID_SIZE * CELL_SIZE }}>
          <div className="static-layer">
            {renderStaticGrid()}
          </div>
          {gameState?.gameOver === 'ongoing' && (
            <div className="dynamic-layer">
              {renderDynamicElements()}
            </div>
          )}
          {gameState?.gameOver === 'win' && (
            <div className="game-over-overlay">
              <div className="confetti-container">
                {[...Array(50)].map((_, i) => (
                  <div key={i} className={`confetti confetti-${i % 5}`} />
                ))}
              </div>
              <div className="victory-message">
                <h2>🏆 Victory! You Found the Treasure! 🏆</h2>
                <div className="button-container">
                  <button className="play-again-button" onClick={resetGame}>
                    Play Again
                  </button>
                  <button className="exit-button" onClick={handleExit}>
                    Exit
                  </button>
                </div>
              </div>
            </div>
          )}
          {gameState?.gameOver === 'lose' && (
            <div className="game-over-overlay">
              <div className="lose-message">
                <h2>💥 Game Over! You Were Caught! 💥</h2>
                <div className="button-container">
                  <button className="play-again-button" onClick={resetGame}>
                    Play Again
                  </button>
                  <button className="exit-button" onClick={handleExit}>
                    Exit
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default App;