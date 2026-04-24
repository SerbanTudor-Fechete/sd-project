export interface Part {
  id: string; 
  name: string;
  price: number;
}

export type CreatePartDto = Omit<Part, 'id'>;
export type UpdatePartDto = Omit<Part, 'id'>;