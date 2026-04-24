export interface Motorcycle {
  id: string;
  brand: string;
  model: string;
  manufactureYear: string;
  licensePlate: string;
}

export type CreateMotorcycleDto = Omit<Motorcycle, 'id'> & { ownerId: string };
export type UpdateMotorcycleDto = Omit<Motorcycle, 'id'> & { ownerId: string };