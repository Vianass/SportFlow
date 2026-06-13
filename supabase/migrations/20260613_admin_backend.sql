-- SportFlow Admin backend requirements.
-- Run this migration in the Supabase SQL editor after reviewing existing policies.

create or replace function public.is_active_admin()
returns boolean
language sql
stable
security definer
set search_path = public, auth
as $$
    select exists (
        select 1
        from public.perfis
        where id = auth.uid()
          and papel = 'ADMIN'
          and estado = 'ATIVO'
    );
$$;

create or replace function public.aprovar_utilizador(user_id uuid)
returns void
language plpgsql
security definer
set search_path = public, auth
as $$
begin
    if not public.is_active_admin() then
        raise exception 'Acesso reservado a administradores ativos';
    end if;

    if user_id = auth.uid() then
        raise exception 'Um administrador não pode alterar a própria conta';
    end if;

    update public.perfis
    set estado = 'ATIVO'
    where id = user_id
      and papel = 'ORGANIZADOR'
      and estado = 'PENDENTE';

    if not found then
        raise exception 'Organizador pendente não encontrado';
    end if;
end;
$$;

create or replace function public.rejeitar_utilizador(user_id uuid)
returns void
language plpgsql
security definer
set search_path = public, auth
as $$
begin
    if not public.is_active_admin() then
        raise exception 'Acesso reservado a administradores ativos';
    end if;

    if user_id = auth.uid() then
        raise exception 'Um administrador não pode alterar a própria conta';
    end if;

    update public.perfis
    set estado = 'REJEITADO'
    where id = user_id
      and papel = 'ORGANIZADOR'
      and estado = 'PENDENTE';

    if not found then
        raise exception 'Organizador pendente não encontrado';
    end if;
end;
$$;

create or replace function public.bloquear_utilizador(user_id uuid)
returns void
language plpgsql
security definer
set search_path = public, auth
as $$
begin
    if not public.is_active_admin() then
        raise exception 'Acesso reservado a administradores ativos';
    end if;

    if user_id = auth.uid() then
        raise exception 'Um administrador não pode bloquear a própria conta';
    end if;

    update public.perfis
    set estado = 'BLOQUEADO'
    where id = user_id
      and papel <> 'ADMIN';

    if not found then
        raise exception 'Utilizador não encontrado ou não bloqueável';
    end if;
end;
$$;

create or replace function public.desbloquear_utilizador(user_id uuid)
returns void
language plpgsql
security definer
set search_path = public, auth
as $$
begin
    if not public.is_active_admin() then
        raise exception 'Acesso reservado a administradores ativos';
    end if;

    if user_id = auth.uid() then
        raise exception 'Um administrador não pode alterar a própria conta';
    end if;

    update public.perfis
    set estado = 'ATIVO'
    where id = user_id
      and papel <> 'ADMIN'
      and estado = 'BLOQUEADO';

    if not found then
        raise exception 'Utilizador bloqueado não encontrado';
    end if;
end;
$$;

revoke all on function public.is_active_admin() from public;
revoke all on function public.aprovar_utilizador(uuid) from public;
revoke all on function public.rejeitar_utilizador(uuid) from public;
revoke all on function public.bloquear_utilizador(uuid) from public;
revoke all on function public.desbloquear_utilizador(uuid) from public;

grant execute on function public.is_active_admin() to authenticated;
grant execute on function public.aprovar_utilizador(uuid) to authenticated;
grant execute on function public.rejeitar_utilizador(uuid) to authenticated;
grant execute on function public.bloquear_utilizador(uuid) to authenticated;
grant execute on function public.desbloquear_utilizador(uuid) to authenticated;

alter table public.perfis enable row level security;

drop policy if exists "profiles_select_own_or_admin" on public.perfis;

create policy "profiles_select_own_or_admin"
on public.perfis
for select
to authenticated
using (
    id = auth.uid()
    or public.is_active_admin()
);

drop policy if exists "admin_select_tournaments" on public.torneios;

create policy "admin_select_tournaments"
on public.torneios
for select
to authenticated
using (
    public.is_active_admin()
);

drop policy if exists "admin_select_games" on public.jogos;

create policy "admin_select_games"
on public.jogos
for select
to authenticated
using (
    public.is_active_admin()
);