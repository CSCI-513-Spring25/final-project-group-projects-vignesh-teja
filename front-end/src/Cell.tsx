import React from 'react';
interface Props {
  value: string;
}
const BoardCell: React.FC<Props> = ({ value }) => {
  return (
    <div className="cell-content">
      {value}
    </div>
  );
};
export default BoardCell;