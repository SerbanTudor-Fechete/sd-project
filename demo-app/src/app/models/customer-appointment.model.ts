
export interface MotorcycleSummary {
  brand: string;
  model: string;
  licensePlate: string;
}

export interface CustomerAppointment {
  id: number;
  appointmentDate: string; 
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  totalCost: number;
  motorcycle: MotorcycleSummary;
}