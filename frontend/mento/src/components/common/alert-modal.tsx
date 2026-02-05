import { CheckCircle2, Info, AlertTriangle, XCircle } from "lucide-react";
import { cn } from "@/lib/utils";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";

export type AlertModalType = "success" | "error" | "warning" | "info";

interface AlertModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  title?: string;
  message: React.ReactNode;
  type?: AlertModalType;
  confirmText?: string;
}

const TYPE_STYLES: Record<AlertModalType, { icon: typeof CheckCircle2; iconClass: string; iconBg: string }> = {
  success: {
    icon: CheckCircle2,
    iconClass: "text-primary-500",
    iconBg: "bg-primary-100/70",
  },
  info: {
    icon: Info,
    iconClass: "text-primary-500",
    iconBg: "bg-primary-100/70",
  },
  warning: {
    icon: AlertTriangle,
    iconClass: "text-yellow-500",
    iconBg: "bg-yellow-100/70",
  },
  error: {
    icon: XCircle,
    iconClass: "text-red-500",
    iconBg: "bg-red-100/70",
  },
};

export function AlertModal({ open, onOpenChange, title = "알림", message, type = "info", confirmText = "확인" }: AlertModalProps) {
  const { icon: Icon, iconClass, iconBg } = TYPE_STYLES[type];

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-md rounded-2xl bg-white p-0 shadow-2xl [&>button]:hidden">
        <div className="px-6 pb-6 pt-7">
          <DialogHeader className="items-center text-center">
            <div className={cn("mb-3 flex h-12 w-12 items-center justify-center rounded-full", iconBg)}>
              <Icon className={cn("h-6 w-6", iconClass)} />
            </div>
            <DialogTitle className="text-xl font-semibold text-dark-bg">{title}</DialogTitle>
          </DialogHeader>
          <div className={cn("mt-3 text-center text-sm text-muted-foreground", typeof message === "string" && "whitespace-pre-line")}>{message}</div>
          <div className="mt-6">
            <Button type="button" onClick={() => onOpenChange(false)} className="h-11 w-full rounded-xl bg-gradient-to-r from-primary-500 to-primary-400 text-dark-bg hover:brightness-105">
              {confirmText}
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
