export type AppointmentStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';

export interface ServiceAppointment {
  id: string;
  scheduleDate: string;
  description: string;
  totalCost: number;
  status: AppointmentStatus;
}

export type CreateAppointmentDto = Omit<ServiceAppointment, 'id'> & { motorcycleId: string, partIds: string[] };
export type UpdateAppointmentDto = Omit<ServiceAppointment, 'id'> & { motorcycleId: string, partIds: string[] };