import { CheckCircle2, Info, AlertTriangle, XCircle } from "lucide-react";
import { cn } from "@/lib/utils";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import type { AlertModalType } from "./alert-modal";

interface ConfirmModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onConfirm: () => void;
  title?: string;
  message: React.ReactNode;
  type?: AlertModalType;
  confirmText?: string;
  cancelText?: string;
  confirmDisabled?: boolean;
  disableClose?: boolean;
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

export function ConfirmModal({
  open,
  onOpenChange,
  onConfirm,
  title = "확인",
  message,
  type = "warning",
  confirmText = "확인",
  cancelText = "취소",
  confirmDisabled = false,
  disableClose = false,
}: ConfirmModalProps) {
  const { icon: Icon, iconClass, iconBg } = TYPE_STYLES[type];

  const handleOpenChange = (nextOpen: boolean) => {
    if (!nextOpen && disableClose) return;
    onOpenChange(nextOpen);
  };

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent
        className="max-w-md rounded-2xl bg-white p-0 shadow-2xl [&>button]:hidden"
        onEscapeKeyDown={(event) => {
          if (disableClose) event.preventDefault();
        }}
        onInteractOutside={(event) => {
          if (disableClose) event.preventDefault();
        }}
      >
        <div className="px-6 pb-6 pt-7">
          <DialogHeader className="items-center text-center">
            <div className={cn("mb-3 flex h-12 w-12 items-center justify-center rounded-full", iconBg)}>
              <Icon className={cn("h-6 w-6", iconClass)} />
            </div>
            <DialogTitle className="text-xl font-semibold text-dark-bg">{title}</DialogTitle>
          </DialogHeader>
          <div className={cn("mt-3 text-center text-sm text-muted-foreground", typeof message === "string" && "whitespace-pre-line")}>{message}</div>
          <div className="mt-6 flex gap-3">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)} disabled={confirmDisabled} className="h-11 flex-1 rounded-xl">
              {cancelText}
            </Button>
            <Button
              type="button"
              onClick={onConfirm}
              disabled={confirmDisabled}
              className={cn(
                "h-11 flex-1 rounded-xl text-white",
                type === "error" ? "bg-red-500 hover:bg-red-600" : "bg-gradient-to-r from-primary-500 to-primary-400 text-dark-bg hover:brightness-105"
              )}
            >
              {confirmText}
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
